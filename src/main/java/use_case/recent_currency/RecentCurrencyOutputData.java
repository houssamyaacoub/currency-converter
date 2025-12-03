package use_case.recent_currency;

import java.util.Collections;
import java.util.List;

/**
 * Output data for the Recent / Frequently Used Currencies use case (Use Case 8).
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
        List<String> safeFavourites;
        if (favouriteCurrencies == null) {
            safeFavourites = List.of();
        } else {
            safeFavourites = List.copyOf(favouriteCurrencies);
        }
        this.favouriteCurrencies = safeFavourites;
        List<String> safeTopFrequent;
        if (topFrequentCurrencies == null) {
            safeTopFrequent = List.of();
        } else {
            safeTopFrequent = List.copyOf(topFrequentCurrencies);
        }
        this.topFrequentCurrencies = safeTopFrequent;
        List<String> safeOrderedList;
        if (orderedCurrencyList == null) {
            safeOrderedList = List.of();
        } else {
            safeOrderedList = List.copyOf(orderedCurrencyList);
        }
        this.orderedCurrencyList = safeOrderedList;
    }

    /**
     * Returns the identifier of the user whose recent currencies are described.
     *
     * @return the user id
     */

    public String getUserId() {
        return userId;
    }

    /**
     * Returns an unmodifiable list of the user's favourite currencies.
     *
     * @return the favourite currency codes
     */

    public List<String> getFavouriteCurrencies() {
        return Collections.unmodifiableList(favouriteCurrencies);
    }

    /**
     * Returns an unmodifiable list of the user's most frequently used currencies.
     *
     * @return the top frequent currency codes
     */

    public List<String> getTopFrequentCurrencies() {
        return Collections.unmodifiableList(topFrequentCurrencies);
    }

    /**
     * Returns the final ordered currency list for dropdowns, starting with favourites
     * and top frequent currencies.
     *
     * @return an unmodifiable ordered currency list
     */

    public List<String> getOrderedCurrencyList() {
        return Collections.unmodifiableList(orderedCurrencyList);
    }
}
