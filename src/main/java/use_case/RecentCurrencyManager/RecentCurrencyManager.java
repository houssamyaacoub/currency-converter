package use_case.RecentCurrencyManager;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * 最近货币选择管理器 - 自动记录用户最近选择的货币
 * 实现LRU（最近最少使用）缓存机制，最多保存5个货币
 */
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

    /**
     * 记录货币选择（当用户选择货币时调用）
     * @param currencyCode 货币代码 (如: USD, EUR, CNY)
     */
    public void recordCurrencySelection(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return;
        }

        String code = currencyCode.toUpperCase().trim();

        // 如果已经存在，先移除再添加到前面（更新位置）
        if (recentCurrencies.contains(code)) {
            recentCurrencies.remove(code);
        }

        // 添加到开头（最新使用的）
        LinkedHashSet<String> newSet = new LinkedHashSet<>();
        newSet.add(code);
        newSet.addAll(recentCurrencies);

        // 保持最大数量限制
        recentCurrencies = newSet;
        if (recentCurrencies.size() > MAX_RECENT_CURRENCIES) {
            // 移除最旧的一个（最少使用的）
            Iterator<String> iterator = recentCurrencies.iterator();
            for (int i = 0; i < MAX_RECENT_CURRENCIES; i++) {
                iterator.next();
            }
            iterator.remove();
        }

        saveRecentCurrencies();
    }

    /**
     * 记录货币转换操作（同时记录基础货币和目标货币）
     */
    public void recordCurrencyConversion(String fromCurrency, String toCurrency) {
        recordCurrencySelection(fromCurrency);
        recordCurrencySelection(toCurrency);
    }

    /**
     * 获取最近使用的货币列表（按使用时间倒序，最新的在前）
     */
    public List<String> getRecentCurrencies() {
        return new ArrayList<>(recentCurrencies);
    }

    /**
     * 获取完整的货币选择列表（最近使用的 + 所有支持的）
     */
    public List<String> getCombinedCurrencyList(List<String> allSupportedCurrencies) {
        List<String> combinedList = new ArrayList<>();

        // 1. 先添加最近使用的货币（最新的在前）
        combinedList.addAll(recentCurrencies);

        // 2. 添加所有支持的货币（排除已经包含的最近使用货币）
        for (String currency : allSupportedCurrencies) {
            if (!recentCurrencies.contains(currency)) {
                combinedList.add(currency);
            }
        }

        return combinedList;
    }

    /**
     * 清除所有最近使用记录
     */
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
            System.err.println("加载最近货币记录失败: " + e.getMessage());
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
            System.err.println("保存最近货币记录失败: " + e.getMessage());
        }
    }

    public int getRecentCount() {
        return recentCurrencies.size();
    }

    public boolean isRecentCurrency(String currencyCode) {
        return recentCurrencies.contains(currencyCode.toUpperCase());
    }
}