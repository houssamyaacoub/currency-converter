package use_case.favourite_currency;

/**
 * Input boundary for the Favourite Currency use case (Use Case 5).
 * The controller calls this interface to request adding or removing
 * a currency from a user's favourites.
 */
public interface FavouriteCurrencyInputBoundary {

    /**
     * Executes the favourite currency use case for the given input data.
     *
     * @param inputData information about which user and which currency
     *                  is being marked or unmarked as favourite.
     */
    void execute(FavouriteCurrencyInputData inputData);
}
