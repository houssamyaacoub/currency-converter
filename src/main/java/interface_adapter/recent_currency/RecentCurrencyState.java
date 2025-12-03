package interface_adapter.recent_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the Recent / Frequently Used Currencies feature (Use Case 8).
 * This holds everything the view needs to display:
 *  - favourite currencies,
 *  - top frequent currencies,
 *  - the final ordered list for dropdowns,
 *  - and any error message.
 */
public class RecentCurrencyState {

    private List<String> favouriteCurrencies = new ArrayList<>();
    private List<String> topFrequentCurrencies = new ArrayList<>();
    private List<String> orderedCurrencyList = new ArrayList<>();
    private String errorMessage = "";

    /**
     * Returns the list of favourite currency codes for the current user.
     *
     * @return a mutable copy of the favourite currencies list.
     */

    public List<String> getFavouriteCurrencies() {
        return favouriteCurrencies;
    }

    /**
     * Sets the list of favourite currency codes.
     * A defensive copy is stored internally so external callers cannot modify
     * the internal list directly.
     *
     * @param favouriteCurrencies the new favourite currency codes; if {@code null},
     *                            this will become an empty list.
     */

    public void setFavouriteCurrencies(List<String> favouriteCurrencies) {
        if (favouriteCurrencies == null) {
            this.favouriteCurrencies = new ArrayList<>();
        }
        else {
            this.favouriteCurrencies = new ArrayList<>(favouriteCurrencies);
        }
    }

    /**
     * Returns the list of top frequent currencies for the current user.
     *
     * @return a mutable copy of the top frequent currencies.
     */

    public List<String> getTopFrequentCurrencies() {
        return topFrequentCurrencies;
    }

    /**
     * Sets the list of top frequent currencies.
     * A defensive copy is stored internally.
     *
     * @param topFrequentCurrencies the new top frequent currencies; if {@code null},
     *                              this will become an empty list.
     */

    public void setTopFrequentCurrencies(List<String> topFrequentCurrencies) {
        if (topFrequentCurrencies == null) {
            this.topFrequentCurrencies = new ArrayList<>();
        }
        else {
            this.topFrequentCurrencies = new ArrayList<>(topFrequentCurrencies);
        }
    }

    /**
     * Returns the final ordered list of currencies for dropdowns or other views.
     *
     * @return a mutable copy of the ordered currency list.
     */

    public List<String> getOrderedCurrencyList() {
        return orderedCurrencyList;
    }

    /**
     * Sets the final ordered list of currencies that the UI should display.
     * A defensive copy is stored internally.
     *
     * @param orderedCurrencyList the new ordered list; if {@code null},
     *                            this will become an empty list.
     */

    public void setOrderedCurrencyList(List<String> orderedCurrencyList) {
        if (orderedCurrencyList == null) {
            this.orderedCurrencyList = new ArrayList<>();
        }
        else {
            this.orderedCurrencyList = new ArrayList<>(orderedCurrencyList);
        }
    }
    /**
     * Returns the current error message, if any.
     *
     * @return the error message; never {@code null}.
     */

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message for this state.
     *
     * @param errorMessage the new error message; if {@code null},
     *                     it will be converted to an empty string.
     */

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            this.errorMessage = "";
        }
        else {
            this.errorMessage = errorMessage;
        }
    }
}
