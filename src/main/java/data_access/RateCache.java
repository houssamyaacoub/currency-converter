package data_access;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RateCache {
    private static final String FILE = "rate_cache.txt";

    public static void save(Map<String, Double> rates, Instant timestamp) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE))) {
            w.write(Long.toString(timestamp.getEpochSecond()));
            w.newLine();

            for (String c : rates.keySet()) {
                w.write(c + "=" + rates.get(c));
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    public static Map<String, Double> loadRates() {
        try (BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            r.readLine(); // skip timestamp line
            Map<String, Double> map = new HashMap<>();

            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split("=");
                map.put(parts[0], Double.parseDouble(parts[1]));
            }
            return map;

        } catch (Exception e) {
            return null;
        }
    }

    public static Instant loadTimestamp() {
        try (BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String ts = r.readLine();
            return Instant.ofEpochSecond(Long.parseLong(ts));
        } catch (Exception e) {
            return null;
        }
    }
}
