package use_case.recent_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Output data for the Recent / Frequently Used Currencies use case.
 * <p>
 * This class defensively copies all lists and removes any null elements
 * so that callers and tests never trigger {@link NullPointerException}
 * inside {@code List.copyOf(...)}.
 */
public class RecentCurrencyOutputData {

    private final String userId;
    private final List<String> favouriteCurrencies;
    private final List<String> topFrequentCurrencies;
    private final List<String> orderedCurrencyList;

    /**
     * Constructs an immutable output data object.
     *
     * @param userId                a non-null user id string (may be empty)
     * @param favouriteCurrencies   may be null or contain null elements
     * @param topFrequentCurrencies may be null or contain null elements
     * @param orderedCurrencyList   may be null or contain null elements
     */
    public RecentCurrencyOutputData(String userId,
                                    List<String> favouriteCurrencies,
                                    List<String> topFrequentCurrencies,
                                    List<String> orderedCurrencyList) {

        this.userId = userId == null ? "" : userId;

        this.favouriteCurrencies = safeCopy(favouriteCurrencies);
        this.topFrequentCurrencies = safeCopy(topFrequentCurrencies);
        this.orderedCurrencyList = safeCopy(orderedCurrencyList);
    }

    /** Returns the user id string (never null). */
    public String getUserId() {
        return userId;
    }

    /** Returns an unmodifiable list without nulls. */
    public List<String> getFavouriteCurrencies() {
        return favouriteCurrencies;
    }

    /** Returns an unmodifiable list without nulls. */
    public List<String> getTopFrequentCurrencies() {
        return topFrequentCurrencies;
    }

    /** Returns an unmodifiable list without nulls. */
    public List<String> getOrderedCurrencyList() {
        return orderedCurrencyList;
    }

    /**
     * Creates an unmodifiable copy of the given list with all null elements removed.
     * If the list itself is null or becomes empty after filtering, an empty list
     * is returned. This prevents {@link NullPointerException} inside List.copyOf.
     */
    private static List<String> safeCopy(List<String> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }

        List<String> cleaned = new ArrayList<>();
        for (String s : source) {
            if (s != null) {
                cleaned.add(s);
            }
        }

        if (cleaned.isEmpty()) {
            return List.of();
        }

        return List.copyOf(cleaned); // unmodifiable
    }
}

