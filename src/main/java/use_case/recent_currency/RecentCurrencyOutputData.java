package use_case.recent_currency;

import java.util.Collections;
import java.util.List;

/**
 * Output data for the Recent / Frequently Used Currencies use case (Use Case 8).
 *
 * The presenter can use this information to update the ViewModel and UI.
 */
public class RecentCurrencyOutputData {

    private final String userId;
    private final List<String> favouriteCurrencies;
    private final List<String> topFrequentCurrencies;
    private final List<String> orderedCurrencyList;

    /**
     * Constructs a new RecentCurrencyOutputData.
     *
     * @param userId                the unique identifier of the user.
     * @param favouriteCurrencies   the user's favourite currencies.
     * @param topFrequentCurrencies the user's most frequently used currencies.
     * @param orderedCurrencyList   the final ordered list used for dropdowns.
     */
    public RecentCurrencyOutputData(String userId,
                                    List<String> favouriteCurrencies,
                                    List<String> topFrequentCurrencies,
                                    List<String> orderedCurrencyList) {
        this.userId = userId;
        this.favouriteCurrencies = favouriteCurrencies == null
                ? List.of()
                : List.copyOf(favouriteCurrencies);
        this.topFrequentCurrencies = topFrequentCurrencies == null
                ? List.of()
                : List.copyOf(topFrequentCurrencies);
        this.orderedCurrencyList = orderedCurrencyList == null
                ? List.of()
                : List.copyOf(orderedCurrencyList);
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getFavouriteCurrencies() {
        return Collections.unmodifiableList(favouriteCurrencies);
    }

    public List<String> getTopFrequentCurrencies() {
        return Collections.unmodifiableList(topFrequentCurrencies);
    }

    public List<String> getOrderedCurrencyList() {
        return Collections.unmodifiableList(orderedCurrencyList);
    }
}
