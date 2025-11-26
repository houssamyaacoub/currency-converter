package data_access;

import use_case.convert.CurrencyRepository;
import entity.Currency;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DAO that implements the CurrencyRepository by reading from a local file.
 * Contains logic to fetch data from the API and populate the local file.
 */
public class CurrencyListDAO implements CurrencyRepository {

    private static final String FILE_PATH = "symbols.json";
    private static final String SYMBOLS_URL = "https://api.exchangeratesapi.io/v1/symbols";
    private static final String API_KEY = "2ff60cc320a08a2913da1c7390ff4dc8"; // Use your actual key

    private final Map<String, Currency> currencyCache = new HashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CurrencyListDAO() {
        // 1. ATTEMPT LOCAL READ FIRST. This is the fastest path.
        loadCurrenciesFromFile();

        // 2. CHECK: If the local read failed to populate the cache (file missing/empty),
        //    THEN initiate the external fetch.
        if (currencyCache.isEmpty()) {
            System.err.println("Local currency cache missing or empty. Triggering API fetch...");
            // This method will perform the slow I/O and internally call loadCurrenciesFromFile() again.
            fetchAndWriteToFile();
        }
    }
    // --- PART 1: Public Fetch/Write Method (Executed once for setup) ---

    /**
     * Fetches the currency list from the external API and writes it to the local file.
     * This method should be called during application setup or initialization if the file is missing/outdated.
     */
    public void fetchAndWriteToFile() {
        if (!currencyCache.isEmpty()) {
            System.out.println("Cache already populated. Skipping API fetch.");
            return;
        }

        System.out.println("Fetching fresh currency symbols from API...");

        String url = String.format("%s?access_key=%s", SYMBOLS_URL, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || !response.body().contains("\"success\":true")) {
                throw new IOException("API request failed with status: " + response.statusCode());
            }

            // Parse the response and write the simplified data to the file
            Map<String, String> symbolMap = parseSymbolsResponseToMap(response.body());
            writeCurrenciesToFile(symbolMap);
            loadCurrenciesFromFile(); // Reload the newly written file

            System.out.println("Successfully fetched and cached " + currencyCache.size() + " currencies.");

        } catch (IOException | InterruptedException e) {
            System.err.println("FATAL: Failed to fetch currency symbols from API. Error: " + e.getMessage());
            // If fetch fails, the cache remains empty, and subsequent lookups will fail gracefully.
        }

    }

    /**
     * Parses the API JSON response into a simple map of Code -> Name.
     */
    private Map<String, String> parseSymbolsResponseToMap(String json) {
        Map<String, String> symbolMap = new HashMap<>();

        // Use regex to robustly find all key-value pairs inside the "symbols" object
        // Pattern: "CODE": "DESCRIPTION"
        Pattern pattern = Pattern.compile("\"([A-Z]{3})\":\\s*\"([^\"]+)\"");

        // Limit search to the "symbols" block if possible
        int symbolsStart = json.indexOf("\"symbols\":");
        if (symbolsStart == -1) return symbolMap;

        Matcher matcher = pattern.matcher(json.substring(symbolsStart));

        while (matcher.find()) {
            String code = matcher.group(1);
            String name = matcher.group(2);
            symbolMap.put(code, name);
        }
        return symbolMap;
    }

    private void writeCurrenciesToFile(Map<String, String> symbolMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
                // Write in a simple, easy-to-read format for the local cache: CODE|NAME
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        }
    }

    // --- PART 2: Repository Contract Implementation (Synchronous Read) ---

    private void loadCurrenciesFromFile() {
        if (!Files.exists(Paths.get(FILE_PATH))) return;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: CODE|NAME
                String[] parts = line.split("\\|");
                if (parts.length == 2) {

                    String rawName = parts[1].trim();
                    String decodedName = decodeUnicodeEscapes(rawName);

                    Currency currency = new Currency(decodedName, parts[0].trim());
                    currencyCache.put(parts[0].trim(), currency);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading local currency symbols file.", e);
        }
    }

    @Override
    public Currency getByCode(String code) {
        Currency currency = currencyCache.get(code.toUpperCase());
        if (currency == null) {
            throw new IllegalArgumentException("Unsupported currency code: " + code + ". Cache size: " + currencyCache.size());
        }
        return currency;
    }

    // ... inside CurrencyListDAO class ...

    @Override
    public Currency getByName(String name) {
        // Iterate through the cache to find the currency with the matching name
        for (Currency currency : currencyCache.values()) {
            if (currency.getName().equalsIgnoreCase(name)) {
                return currency;
            }
        }
        // Fallback or error handling
        throw new IllegalArgumentException("Currency name not found: " + name);
    }

    public List<Currency> getAllCurrencies() {
        // This is a synchronous read from the cache built by loadCurrenciesFromFile()
        return new ArrayList<>(currencyCache.values());
    }
    /**
     * Decode unicode escape sequences like "\\u00e3" into real characters (ã).
     */
    private String decodeUnicodeEscapes(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ) {
            char c = input.charAt(i);
            if (c == '\\' && i + 5 < input.length() && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                try {
                    int codePoint = Integer.parseInt(hex, 16);
                    sb.append((char) codePoint);
                    i += 6;
                    continue;
                } catch (NumberFormatException e) {
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }


}