package use_case.offline_viewing;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple file-based cache for latest exchange rates.
 * File format:
 *   line 1: epochSeconds (last updated time)
 *   next lines: CODE=rate
 */
public class RateCache {

    private static final String FILE_NAME = "rate_cache.txt";

    /**
     * Save the given map of rates and timestamp to a local text file.
     * This is called from ONLINE code after a successful API call.
     */
    public static void save(Map<String, Double> rates, Instant timestamp) {
        if (rates == null || rates.isEmpty() || timestamp == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // first line: epoch seconds
            writer.write(Long.toString(timestamp.getEpochSecond()));
            writer.newLine();

            // next lines: CODE=rate
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }

        } catch (IOException e) {
            // cache failure should NOT break the app
            System.err.println("Warning: failed to write rate cache: " + e.getMessage());
        }
    }

    /**
     * Low-level loader returning both rates and timestamp.
     */
    public static CachedRates load() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.isEmpty()) {
                return null;
            }

            long epoch = Long.parseLong(firstLine.trim());
            Instant ts = Instant.ofEpochSecond(epoch);

            Map<String, Double> rates = new HashMap<>();
            String line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                int eq = line.indexOf('=');
                if (eq > 0 && eq < line.length() - 1) {
                    String code = line.substring(0, eq).trim();
                    String valueStr = line.substring(eq + 1).trim();
                    try {
                        double v = Double.parseDouble(valueStr);
                        rates.put(code, v);
                    } catch (NumberFormatException ignored) {
                        // skip bad lines
                    }
                }
                line = reader.readLine();
            }

            if (rates.isEmpty()) {
                return null;
            }

            return new CachedRates(rates, ts);

        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: failed to read rate cache: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convenience: just returns the rates map, or null if nothing cached.
     */
    public static Map<String, Double> loadRates() {
        CachedRates cached = load();
        return cached == null ? null : cached.rates;
    }

    /**
     * Convenience: just returns the timestamp, or null if nothing cached.
     */
    public static Instant loadTimestamp() {
        CachedRates cached = load();
        return cached == null ? null : cached.timestamp;
    }

    public static class CachedRates {
        public final Map<String, Double> rates;
        public final Instant timestamp;

        public CachedRates(Map<String, Double> r, Instant t) {
            this.rates = r;
            this.timestamp = t;
        }
    }
}
