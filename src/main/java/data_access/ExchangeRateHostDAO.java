package data_access;

import data_access.offline_viewing.PairRateCache;
import use_case.convert.ExchangeRateDataAccessInterface;
import use_case.convert.CurrencyRepository;

import entity.CurrencyConversion;
import entity.Currency;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateHostDAO implements ExchangeRateDataAccessInterface {

    private static final String BASE_URL = "https://api.exchangeratesapi.io/latest";
    private static final String API_KEY = "574aee8be3b66df24789719d4011563c";

    private final HttpClient httpClient;
    private final CurrencyRepository currencyLookup;

    // NEW: pair cache for offline conversions
    private final PairRateCache pairRateCache =
            new PairRateCache(PairRateCache.DEFAULT_FILENAME);

    public ExchangeRateHostDAO(CurrencyRepository currencyLookup) {
        this.httpClient = HttpClient.newHttpClient();
        this.currencyLookup = currencyLookup;
    }

    // ========== Connectivity helper ==========

    private boolean hasInternetConnection() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("HEAD");
            int code = conn.getResponseCode();
            return 200 <= code && code < 400;
        } catch (Exception e) {
            return false;
        }
    }

    // ========== Latest rate (used by Convert use case) ==========

    @Override
    public CurrencyConversion getLatestRate(Currency from, Currency to) {

        String fromCode = from.getSymbol();
        String toCode = to.getSymbol();

        // ---- ONLINE PATH ----
        if (hasInternetConnection()) {
            // 1. Construct the API URL
            String url = String.format("%s?symbols=%s,%s&access_key=%s",
                    BASE_URL, fromCode, toCode, API_KEY);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            try {
                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "API request failed with status code: " + response.statusCode());
                }

                // Parse JSON and build CurrencyConversion
                CurrencyConversion conversion =
                        parseJsonResponse(response.body(), from, to);

                // NEW: cache this pair for offline usage
                pairRateCache.put(
                        fromCode,
                        toCode,
                        conversion.getRate(),       // assuming getter exists
                        conversion.getTimeStamp()   // assuming getter exists
                );

                return conversion;

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(
                        "Network or I/O error while accessing exchange rate API.", e);
            }
        }

        // ---- OFFLINE PATH ----
        Double cachedRate = pairRateCache.getRate(fromCode, toCode);

        if (cachedRate != null) {
            Instant ts = pairRateCache.getTimestamp(fromCode, toCode);
            if (ts == null) {
                ts = Instant.now(); // fallback if somehow missing
            }
            // Build a CurrencyConversion using cached data
            return new CurrencyConversion(from, to, cachedRate, ts);
        }

        // No cached rate for this pair → tell upper layers to show error
        throw new RuntimeException(
                "Offline mode: no cached rate for " + fromCode + " -> " + toCode);
    }

    // ========== Helper for JSON parsing ==========

    //Helper method to getLatestRate()
    private CurrencyConversion parseJsonResponse(String json, Currency from, Currency to) {

        if (json.contains("\"success\":false") || json.contains("\"error\"")) {
            throw new RuntimeException("API returned failure: " + json);
        }

        try {
            // Extract Timestamp (API provides 'date' as YYYY-MM-DD string)
            String dateKey = "\"date\":\"";
            int dateStart = json.indexOf(dateKey) + dateKey.length();
            int dateEnd = json.indexOf("\"", dateStart);
            String dateString = json.substring(dateStart, dateEnd);

            Instant timestamp = LocalDate.parse(dateString)
                    .atStartOfDay().toInstant(ZoneOffset.UTC);

            // Extract Rates using the currency codes (as required by the API response structure)
            String ratesBlock = json.substring(
                    json.indexOf("\"rates\":") + 8,
                    json.indexOf("}", json.indexOf("\"rates\":"))
            );

            double rateFromBase = extractRate(ratesBlock, from.getSymbol());
            double rateToTarget = extractRate(ratesBlock, to.getSymbol());

            // Calculate Cross-Rate: Rate (From -> To) = Rate_To / Rate_From
            double finalRate = rateToTarget / rateFromBase;

            // Instantiate and return the core CurrencyConversion Entity
            return new CurrencyConversion(from, to, finalRate, timestamp);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse API response or calculate rate: " + e.getMessage(), e);
        }
    }

    // Helper method to parseJsonResponse()
    private double extractRate(String ratesBlock, String currencyCode) {

        String codeKey = "\"" + currencyCode + "\":";
        int rateStart = ratesBlock.indexOf(codeKey);

        if (rateStart == -1) {
            throw new RuntimeException(
                    "Currency code " + currencyCode + " not found in rates block.");
        }

        rateStart += codeKey.length();

        int rateEnd = ratesBlock.indexOf(",", rateStart);
        if (rateEnd == -1) {
            rateEnd = ratesBlock.indexOf("}", rateStart);
        }
        if (rateEnd == -1) {
            rateEnd = ratesBlock.length();
        }

        return Double.parseDouble(ratesBlock.substring(rateStart, rateEnd));
    }

    // ========== Historical API (unchanged except using same HttpClient) ==========

    @Override
    public List<CurrencyConversion> getHistoricalRates(
            Currency from, Currency to, LocalDate start, LocalDate end) {

        List<CurrencyConversion> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        int step;
        if (daysBetween > 364) {        // year
            step = 14;
        } else if (daysBetween > 179) { // 6 months
            step = 7;
        } else if (daysBetween > 29) {  // 1 month
            step = 5;
        } else {                        // 1 week
            step = 1;
        }

        LocalDate current = start;

        while (!current.isAfter(end)) {
            String dateString = current.format(formatter);

            String url = String.format(
                    "https://api.exchangeratesapi.io/v1/%s?access_key=%s&symbols=%s,%s",
                    dateString, API_KEY, from.getSymbol(), to.getSymbol());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    CurrencyConversion conversion =
                            parseJsonResponse(response.body(), from, to);
                    resultList.add(conversion);
                }
            } catch (Exception e) {
                System.out.println("Skipped " + dateString + ": " + e.getMessage());
            }

            current = current.plusDays(step);
        }
        return resultList;
    }
}
