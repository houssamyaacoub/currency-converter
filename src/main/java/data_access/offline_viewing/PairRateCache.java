package data_access.offline_viewing;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * File-based cache for individual currency pairs.
 *
 * File format (CSV, one entry per line):
 *   FROM,TO,RATE,EPOCH_SECONDS
 */
public class PairRateCache {

    public static final String DEFAULT_FILENAME = "offline_pairs.csv";

    private static final class Entry {
        final double rate;
        final Instant timestamp;

        Entry(double rate, Instant timestamp) {
            this.rate = rate;
            this.timestamp = timestamp;
        }
    }

    private final File file;
    // key = "FROM->TO"
    private final Map<String, Entry> cache = new HashMap<>();

    public PairRateCache(String filename) {
        this.file = new File(filename);
        loadFromDisk();
    }

    private String key(String from, String to) {
        return from + "->" + to;
    }

    /** Load cache from disk into memory. */
    private void loadFromDisk() {
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String from = parts[0];
                String to   = parts[1];
                double rate = Double.parseDouble(parts[2]);
                long epoch  = Long.parseLong(parts[3]);

                cache.put(key(from, to),
                        new Entry(rate, Instant.ofEpochSecond(epoch)));
            }
        } catch (IOException | NumberFormatException ignored) {
            // worst-case: cache is empty
        }
    }

    /** Persist all cached pairs to disk. */
    private void saveToDisk() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Entry> e : cache.entrySet()) {
                String k = e.getKey();
                int idx = k.indexOf("->");
                String from = k.substring(0, idx);
                String to   = k.substring(idx + 2);
                Entry entry = e.getValue();

                bw.write(from + "," + to + "," + entry.rate + "," +
                        entry.timestamp.getEpochSecond());
                bw.newLine();
            }
        } catch (IOException ignored) {
            // if saving fails, we just lose offline data
        }
    }

    /** Called when an ONLINE conversion succeeds. */
    public synchronized void put(String from, String to,
                                 double rate, Instant timestamp) {
        cache.put(key(from, to), new Entry(rate, timestamp));
        saveToDisk();
    }

    /** Returns cached rate, or null if pair never saved. */
    public synchronized Double getRate(String from, String to) {
        Entry e = cache.get(key(from, to));
        return e == null ? null : e.rate;
    }

    /** Returns cached timestamp, or null if pair never saved. */
    public synchronized Instant getTimestamp(String from, String to) {
        Entry e = cache.get(key(from, to));
        return e == null ? null : e.timestamp;
    }

    /** Returns a copy of all cached pairs. */
    public synchronized Map<String, Double> getAllRates() {
        Map<String, Double> copy = new HashMap<>();
        for (Map.Entry<String, Entry> e : cache.entrySet()) {
            copy.put(e.getKey(), e.getValue().rate);
        }
        return copy;
    }

    /** Returns the most recent timestamp among all pairs (or null if empty). */
    public synchronized Instant getLatestTimestamp() {
        Instant latest = null;
        for (Entry e : cache.values()) {
            if (latest == null || e.timestamp.isAfter(latest)) {
                latest = e.timestamp;
            }
        }
        return latest;
    }
}
