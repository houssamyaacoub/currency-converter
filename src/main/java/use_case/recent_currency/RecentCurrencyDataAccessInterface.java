package use_case.recent_currency;

import java.util.List;
import java.util.Map;

/**
 * Data access interface for the Recent / Frequently Used Currencies use case (Use Case 8).
 * Implementations of this interface belong in the data_access layer and may use
 * Preferences, files, or a database to store usage counts.
 * They may also delegate to other gateways (for favourites and supported currencies).
 */
public interface RecentCurrencyDataAccessInterface {

    /**
     * Returns true if a user with the given ID exists.
     *
     * @param userId the unique identifier of the user.
     * @return true if the user exists; false otherwise.
     */
    boolean userExists(String userId);

    /**
     * Records one usage of the given currency for the given user.
     *
     * @param userId       the unique identifier of the user.
     * @param currencyCode the currency code that was used.
     */
    void recordUsage(String userId, String currencyCode);

    /**
     * Returns a mapping from currencyCode to usageCount for the given user.
     *
     * @param userId the unique identifier of the user.
     * @return a map of currency codes to usage counts.
     */
    Map<String, Integer> getUsageCounts(String userId);

    /**
     * Returns the user's favourite currencies.
     * This can be implemented by delegating to the FavouriteCurrencyDataAccessInterface.
     *
     * @param userId the unique identifier of the user.
     * @return a list of favourite currency codes.
     */
    List<String> getFavouriteCurrencies(String userId);

    /**
     * Returns all supported currency codes, e.g. ["CAD", "USD", "EUR", ...].
     *
     * @return a list of all supported currency codes.
     */
    List<String> getAllSupportedCurrencies();
    /**
     * Returns a user-specific ordered currency list for use in dropdowns.
     * Typically, favourite currencies should appear first, followed by all others.
     *
     * @param userId the unique identifier of the user
     * @return an ordered list of currency strings
     */

    List<String> getOrderedCurrenciesForUser(String userId);
}
