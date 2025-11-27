package interface_adapter.recent_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the Recent / Frequently Used Currencies feature (Use Case 8).
 *
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

    public List<String> getFavouriteCurrencies() {
        return favouriteCurrencies;
    }

    public void setFavouriteCurrencies(List<String> favouriteCurrencies) {
        this.favouriteCurrencies = favouriteCurrencies == null
                ? new ArrayList<>()
                : new ArrayList<>(favouriteCurrencies);
    }

    public List<String> getTopFrequentCurrencies() {
        return topFrequentCurrencies;
    }

    public void setTopFrequentCurrencies(List<String> topFrequentCurrencies) {
        this.topFrequentCurrencies = topFrequentCurrencies == null
                ? new ArrayList<>()
                : new ArrayList<>(topFrequentCurrencies);
    }

    public List<String> getOrderedCurrencyList() {
        return orderedCurrencyList;
    }

    public void setOrderedCurrencyList(List<String> orderedCurrencyList) {
        this.orderedCurrencyList = orderedCurrencyList == null
                ? new ArrayList<>()
                : new ArrayList<>(orderedCurrencyList);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }
}
