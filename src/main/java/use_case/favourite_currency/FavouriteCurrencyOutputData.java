package use_case.favourite_currency;

import java.util.Collections;
import java.util.List;

/**
 * Output data for the Favourite Currency use case (Use Case 5).
 * This object is created by the interactor and sent to the presenter.
 */
public class FavouriteCurrencyOutputData {

    private final String userId;
    private final List<String> favouriteCurrencies;

    /**
     * Constructs a new FavouriteCurrencyOutputData.
     *
     * @param userId              the unique identifier of the user.
     * @param favouriteCurrencies the updated list of favourite currencies.
     */
    public FavouriteCurrencyOutputData(String userId, List<String> favouriteCurrencies) {
        this.userId = userId;

        List<String> safeFavourites;
        if (favouriteCurrencies == null) {
            safeFavourites = List.of();
        } else {
            safeFavourites = List.copyOf(favouriteCurrencies);
        }

        this.favouriteCurrencies = safeFavourites;
    }

    /**
     * Returns the identifier of the user whose favourite list is represented
     * by this output data.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns an unmodifiable list of favourite currency codes.
     *
     * @return the user's favourite currencies.
     */
    public List<String> getFavouriteCurrencies() {
        return Collections.unmodifiableList(favouriteCurrencies);
    }
}
