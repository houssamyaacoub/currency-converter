package data_access;

import use_case.convert.CurrencyRepository;
import entity.Currency;
import entity.CurrencyFactory;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data Access Object (DAO) for Currency Data.
 * This class implements the {@link CurrencyRepository} interface, adhering to the
 * Clean Architecture Data Access layer. It employs a "Self-Healing" strategy:
 * 1. It attempts to load currency data from a local cache file for speed.
 * 2. If the file is missing or empty, it automatically fetches data from the external API
 * and populates the local file for future runs.
 */
public class CurrencyListDAO implements CurrencyRepository {

    private static final String FILE_PATH = "symbols.txt";
    private static final String SYMBOLS_URL = "https://api.exchangeratesapi.io/v1/symbols";
    private static final String API_KEY = "612b9a2f977e9348a53ce666b1901cd9";

    private final Map<String, Currency> currencyCache = new HashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final CurrencyFactory currencyFactory = new CurrencyFactory();

    /**
     * Constructs the CurrencyListDAO.
     * Initializes the data source by attempting to read from the local file.
     * If the local cache remains empty after the attempt, it triggers a network fetch.
     */
    public CurrencyListDAO() {
        // 1. Attempt fast local read
        loadCurrenciesFromFile();

        // 2. Self-Healing: If local read failed, fetch from API
        if (currencyCache.isEmpty()) {
            System.err.println("Local currency cache missing or empty. Triggering API fetch...");
            fetchAndWriteToFile();
        }
    }

    // --- PART 1: External Data Fetching & Persisting ---

    /**
     * Fetches the list of supported currencies from the external API and writes them
     * to the local cache file.
     * This method performs network I/O and File I/O. It updates the in-memory cache
     * upon completion.
     */
    public void fetchAndWriteToFile() {
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

            // Parse JSON response into a map
            Map<String, String> symbolMap = parseSymbolsResponseToMap(response.body());

            // Persist to local file
            writeCurrenciesToFile(symbolMap);

            // Reload cache from the newly written file to ensure consistency
            loadCurrenciesFromFile();

            System.out.println("Successfully fetched and cached " + currencyCache.size() + " currencies.");

        } catch (IOException | InterruptedException e) {
            System.err.println("FATAL: Failed to fetch currency symbols from API. Error: " + e.getMessage());
            // The application will continue, but currency lookups may fail if cache is empty.
        }
    }

    /**
     * Parses the raw JSON response from the API to extract currency codes and names.
     * Uses Regex to avoid heavy JSON library dependencies for this specific task.
     *
     * @param json The raw JSON string.
     * @return A Map where Key = Currency Code (e.g., "USD") and Value = Name (e.g., "United States Dollar").
     */
    private Map<String, String> parseSymbolsResponseToMap(String json) {
        Map<String, String> symbolMap = new HashMap<>();

        // Regex pattern to find "CODE": "Description"
        Pattern pattern = Pattern.compile("\"([A-Z]{3})\":\\s*\"([^\"]+)\"");

        // Optimization: Limit search to the "symbols" block
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

    /**
     * Writes the parsed currency map to a local file using a pipe-delimited format.
     *
     * @param symbolMap The map of currency data.
     * @throws IOException If writing to the file fails.
     */
    private void writeCurrenciesToFile(Map<String, String> symbolMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
                // Format: CODE|NAME
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        }
    }

    // --- PART 2: Repository Contract Implementation ---

    /**
     * Loads currency data from the local text file into the in-memory cache.
     * Uses the {@link CurrencyFactory} to instantiate entities.
     */
    private void loadCurrenciesFromFile() {
        if (!Files.exists(Paths.get(FILE_PATH))) return;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Expected Format: CODE|NAME
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String code = parts[0].trim();
                    String rawName = parts[1].trim();

                    // Decode potential unicode characters (e.g., for currencies like Real)
                    String decodedName = decodeUnicodeEscapes(rawName);

                    // Use Factory to ensure consistent Entity creation
                    Currency currency = currencyFactory.create(decodedName, code);
                    currencyCache.put(code, currency);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading local currency symbols file.", e);
        }
    }

    /**
     * Retrieves a Currency Entity by its code.
     *
     * @param code The 3-letter ISO currency code (e.g., "USD").
     * @return The Currency entity.
     * @throws IllegalArgumentException if the code is not found in the cache.
     */
    @Override
    public Currency getByCode(String code) {
        Currency currency = currencyCache.get(code.toUpperCase());
        if (currency == null) {
            throw new IllegalArgumentException("Unsupported currency code: " + code);
        }
        return currency;
    }

    /**
     * Retrieves a Currency Entity by its full name.
     * This operation requires iterating through the cache, so it is O(n).
     *
     * @param name The full name of the currency.
     * @return The Currency entity.
     * @throws IllegalArgumentException if the name is not found.
     */
    @Override
    public Currency getByName(String name) {
        for (Currency currency : currencyCache.values()) {
            if (currency.getName().equalsIgnoreCase(name)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Currency name not found: " + name);
    }

    /**
     * Returns an iterator over the currency values.
     * This supports the Iterator Design Pattern for external traversal.
     *
     * @return An Iterator of Currency objects.
     */
    @Override
    public Iterator<Currency> getCurrencyIterator() {
        return currencyCache.values().iterator();
    }

    /**
     * Returns a list of all currencies.
     *
     * @return A List containing all cached Currency entities.
     */
    @Override
    public List<Currency> getAllCurrencies() {
        return new ArrayList<>(currencyCache.values());
    }

    /**
     * Helper method to decode unicode escape sequences like "\\u00e3" into real characters.
     * Useful because raw API responses might contain escaped characters.
     *
     * @param input The string containing unicode escapes.
     * @return The decoded string.
     */
    private String decodeUnicodeEscapes(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ) {
            char c = input.charAt(i);
            if (c == '\\' && i + 5 < input.length() && input.charAt(i + 1) == 'u') {
                try {
                    String hex = input.substring(i + 2, i + 6);
                    int codePoint = Integer.parseInt(hex, 16);
                    sb.append((char) codePoint);
                    i += 6;
                    continue;
                } catch (NumberFormatException ignored) {
                    // If parsing fails, treat as literal characters
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}
