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

    public List<String> getFavouriteCurrencies() {
        return favouriteCurrencies;
    }

    public void setFavouriteCurrencies(List<String> favouriteCurrencies) {
        this.favouriteCurrencies = favouriteCurrencies == null
                ? new ArrayList<>()
                : new ArrayList<>(favouriteCurrencies);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }
}
