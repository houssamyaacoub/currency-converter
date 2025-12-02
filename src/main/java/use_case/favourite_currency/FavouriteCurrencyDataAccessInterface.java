package use_case.favourite_currency;

import java.util.List;

/**
 * Data access interface for the Favourite Currency use case (Use Case 5).
 * Implementations of this interface belong in the data_access layer and
 * are responsible for persisting and retrieving favourite currencies.
 */
public interface FavouriteCurrencyDataAccessInterface {

    /**
     * Returns true if a user with the given ID exists.
     *
     * @param userId the unique identifier of the user.
     * @return true if the user exists; false otherwise.
     */
    boolean userExists(String userId);

    /**
     * Returns true if the given currency code exists in the system.
     *
     * @param currencyCode the currency code (for example, {@code "CAD"}, {@code "USD"}).
     * @return true if the currency exists; false otherwise.
     */
    boolean currencyExists(String currencyCode);

    /**
     * Returns the list of favourite currency codes for the given user.
     *
     * @param userId the unique identifier of the user.
     * @return a list of favourite currency codes (e.g., ["CAD", "USD"]).
     */
    List<String> getFavouritesForUser(String userId);

    /**
     * Saves the full list of favourite currency codes for the given user.
     *
     * @param userId     the unique identifier of the user.
     * @param favourites the favourite currencies to store.
     */
    void saveFavouritesForUser(String userId, List<String> favourites);
}
