package use_case.RecentCurrencyManager;

import java.util.*;
import java.util.prefs.Preferences;

public class RecentCurrencyManager {
    private static final int MAX_RECENT_CURRENCIES = 5;
    private static final String PREF_KEY = "recent_currencies";
    private LinkedHashSet<String> recentCurrencies;
    private Preferences prefs;

    public RecentCurrencyManager() {
        this.prefs = Preferences.userNodeForPackage(RecentCurrencyManager.class);
        this.recentCurrencies = new LinkedHashSet<>();
        loadRecentCurrencies();
    }

    public void recordCurrencySelection(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return;
        }

        String code = currencyCode.toUpperCase().trim();

        if (recentCurrencies.contains(code)) {
            recentCurrencies.remove(code);
        }

        LinkedHashSet<String> newSet = new LinkedHashSet<>();
        newSet.add(code);
        newSet.addAll(recentCurrencies);

        recentCurrencies = newSet;
        if (recentCurrencies.size() > MAX_RECENT_CURRENCIES) {
            Iterator<String> iterator = recentCurrencies.iterator();
            for (int i = 0; i < MAX_RECENT_CURRENCIES; i++) {
                iterator.next();
            }
            iterator.remove();
        }

        saveRecentCurrencies();
    }

    public void recordCurrencyConversion(String fromCurrency, String toCurrency) {
        recordCurrencySelection(fromCurrency);
        recordCurrencySelection(toCurrency);
    }

    public List<String> getRecentCurrencies() {
        return new ArrayList<>(recentCurrencies);
    }

    public List<String> getCombinedCurrencyList(List<String> allSupportedCurrencies) {
        List<String> combinedList = new ArrayList<>();

        combinedList.addAll(recentCurrencies);
        for (String currency : allSupportedCurrencies) {
            if (!recentCurrencies.contains(currency)) {
                combinedList.add(currency);
            }
        }

        return combinedList;
    }

    public void clearRecentCurrencies() {
        recentCurrencies.clear();
        saveRecentCurrencies();
    }

    private void loadRecentCurrencies() {
        try {
            String saved = prefs.get(PREF_KEY, "");
            if (!saved.isEmpty()) {
                String[] currencies = saved.split(",");
                for (String currency : currencies) {
                    if (!currency.trim().isEmpty()) {
                        recentCurrencies.add(currency.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("failure: " + e.getMessage());
            recentCurrencies = new LinkedHashSet<>();
        }
    }

    private void saveRecentCurrencies() {
        try {
            StringBuilder sb = new StringBuilder();
            for (String currency : recentCurrencies) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(currency);
            }
            prefs.put(PREF_KEY, sb.toString());
            prefs.flush();
        } catch (Exception e) {
            System.err.println("failure: " + e.getMessage());
        }
    }

    public int getRecentCount() {
        return recentCurrencies.size();
    }

    public boolean isRecentCurrency(String currencyCode) {
        return recentCurrencies.contains(currencyCode.toUpperCase());
    }
}
