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

/**
 * Data Access Object (DAO) for fetching exchange rates from the ExchangeRatesAPI.io service.
 * This class implements the {@link ExchangeRateDataAccessInterface}, providing methods
 * to retrieve both the latest and historical exchange rates.
 * <strong>Features:</strong>
 * <ul>
 * <li>Fetches real-time rates via HTTP requests.</li>
 * <li>Supports offline mode by caching successful conversions in a local {@link PairRateCache}.</li>
 * <li>Automatically falls back to cached rates if no internet connection is detected.</li>
 * </ul>
 */
public class ExchangeRateHostDAO implements ExchangeRateDataAccessInterface {

    private static final String BASE_URL = "https://api.exchangeratesapi.io/latest";
    // NOTE: API Key should ideally be loaded from environment variables or a secure config file.
    private static final String API_KEY = "c3c0e3d86fae33b0c35114cfab615717";

    private final HttpClient httpClient;
    // While this field is not strictly used in the methods below, it may be required
    // for future logic involving currency validation or enrichment.
    private final CurrencyRepository currencyLookup;

    // Local cache for storing rates to support offline functionality
    private final PairRateCache pairRateCache =
            new PairRateCache(PairRateCache.DEFAULT_FILENAME);

    /**
     * Constructs a new ExchangeRateHostDAO.
     *
     * @param currencyLookup The repository used for looking up currency details.
     */
    public ExchangeRateHostDAO(CurrencyRepository currencyLookup) {
        this.httpClient = HttpClient.newHttpClient();
        this.currencyLookup = currencyLookup;
    }

    // ========== Connectivity helper ==========

    /**
     * Checks for an active internet connection by pinging a reliable server.
     *
     * @return {@code true} if the connection is successful, {@code false} otherwise.
     */
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

    /**
     * Retrieves the latest exchange rate between two currencies.
     * If an internet connection is available, it fetches the rate from the API
     * and updates the local cache. If offline, it attempts to retrieve the rate
     * from the cache.
     *
     * @param from The source currency.
     * @param to   The target currency.
     * @return A {@link CurrencyConversion} entity containing the rate and timestamp.
     * @throws RuntimeException if the API request fails online, or if no cached rate exists offline.
     */
    @Override
    public CurrencyConversion getLatestRate(Currency from, Currency to) {

        // Using getCode() (via getSymbol()) assuming Symbol field stores the ISO code (e.g., "USD")
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

                // Parse JSON and build CurrencyConversion entity
                CurrencyConversion conversion =
                        parseJsonResponse(response.body(), from, to);

                // Update the offline cache with the fresh rate
                pairRateCache.put(
                        fromCode,
                        toCode,
                        conversion.getRate(),
                        conversion.getTimeStamp()
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
                ts = Instant.now(); // Fallback timestamp if missing
            }
            // Build a CurrencyConversion entity using cached data
            return new CurrencyConversion(from, to, cachedRate, ts);
        }

        // Failure: No internet and no cached data for this pair
        throw new RuntimeException(
                "Offline mode: no cached rate available for " + fromCode + " -> " + toCode);
    }

    // ========== Helper for JSON parsing ==========

    /**
     * Parses the JSON response from the API to construct a CurrencyConversion object.
     * Calculates the cross-rate if the base currency returned by the API is not the requested source currency.
     *
     * @param json The raw JSON response string.
     * @param from The source currency.
     * @param to   The target currency.
     * @return The constructed {@link CurrencyConversion} entity.
     */
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

            // Extract the "rates" block to parse individual values
            String ratesBlock = json.substring(
                    json.indexOf("\"rates\":") + 8,
                    json.indexOf("}", json.indexOf("\"rates\":"))
            );

            double rateFromBase = extractRate(ratesBlock, from.getSymbol());
            double rateToTarget = extractRate(ratesBlock, to.getSymbol());

            // Calculate Cross-Rate: Rate (From -> To) = Rate_To / Rate_From
            // This handles cases where the API returns rates relative to a fixed base (e.g., EUR)
            double finalRate = rateToTarget / rateFromBase;

            return new CurrencyConversion(from, to, finalRate, timestamp);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse API response or calculate rate: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts a specific currency rate from the JSON "rates" block substring.
     *
     * @param ratesBlock   The substring containing the rates (e.g., "USD":1.12,"GBP":0.85).
     * @param currencyCode The code of the currency to find.
     * @return The rate as a double.
     */
    private double extractRate(String ratesBlock, String currencyCode) {

        String codeKey = "\"" + currencyCode + "\":";
        int rateStart = ratesBlock.indexOf(codeKey);

        if (rateStart == -1) {
            throw new RuntimeException(
                    "Currency code " + currencyCode + " not found in rates block.");
        }

        // Move pointer to the start of the numeric value
        rateStart += codeKey.length();

        // Find the end of the value (marked by a comma or closing brace)
        int rateEnd = ratesBlock.indexOf(",", rateStart);
        if (rateEnd == -1) {
            rateEnd = ratesBlock.indexOf("}", rateStart);
        }
        if (rateEnd == -1) {
            // If neither is found, the number extends to the end of the string
            rateEnd = ratesBlock.length();
        }

        return Double.parseDouble(ratesBlock.substring(rateStart, rateEnd));
    }

    // ========== Historical API ==========

    /**
     * Fetches historical exchange rates for a specific currency pair over a date range.
     * To respect API limits and performance, this method samples rates at different intervals
     * (daily, weekly, monthly) depending on the length of the requested range.
     *
     * @param from  The source currency.
     * @param to    The target currency.
     * @param start The start date (inclusive).
     * @param end   The end date (inclusive).
     * @return A list of {@link CurrencyConversion} entities representing the historical rates.
     */
    @Override
    public List<CurrencyConversion> getHistoricalRates(
            Currency from, Currency to, LocalDate start, LocalDate end) {

        List<CurrencyConversion> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        int step;

        // Determine sampling step to avoid excessive API calls
        if (daysBetween > 364) {        // > 1 year: Sample every 2 weeks
            step = 14;
        } else if (daysBetween > 179) { // > 6 months: Sample weekly
            step = 7;
        } else if (daysBetween > 29) {  // > 1 month: Sample every 5 days
            step = 5;
        } else {                        // <= 1 month: Daily
            step = 1;
        }

        LocalDate current = start;

        while (!current.isAfter(end)) {
            String dateString = current.format(formatter);

            // Note: Historical endpoint structure varies by API plan;
            // using the standard /v1/{date} format here.
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
                // Log and skip failed dates to return partial results rather than total failure
                System.out.println("Skipped " + dateString + ": " + e.getMessage());
            }

            current = current.plusDays(step);
        }
        return resultList;
    }
}