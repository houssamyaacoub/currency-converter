package interface_adapter.favourite_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the Favourite Currency feature (Use Case 5).
 *
 * This holds all the data the view needs to display favourite currencies
 * and any error messages.
 */
public class FavouriteCurrencyState {

    private List<String> favouriteCurrencies = new ArrayList<>();
    private String errorMessage = "";

    /**
     * Returns a copy of the current list of favourite currencies.
     *
     * @return a mutable copy of the favourite currency codes.
     */

    public List<String> getFavouriteCurrencies() {
        return favouriteCurrencies;
    }

    /**
     * Sets the list of favourite currencies.
     * A defensive copy is stored internally so that external callers
     * cannot modify the internal list by holding on to the reference.
     *
     * @param favouriteCurrencies the new list of favourite currency codes;
     *                            if {@code null}, this becomes an empty list.
     */

    public void setFavouriteCurrencies(List<String> favouriteCurrencies) {
        this.favouriteCurrencies = favouriteCurrencies == null
                ? new ArrayList<>()
                : new ArrayList<>(favouriteCurrencies);
    }

    /**
     * Returns the current error message to be shown by the UI.
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
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }
}
