package interface_adapter.favourite_currency;

import use_case.favourite_currency.FavouriteCurrencyInputBoundary;
import use_case.favourite_currency.FavouriteCurrencyInputData;

/**
 * Controller for the Favourite Currency feature (Use Case 5).
 * The view calls this controller when the user clicks a "favourite" button
 * (for example, a star icon) to add or remove a currency from favourites.
 */
public class FavouriteCurrencyController {

    private final FavouriteCurrencyInputBoundary interactor;

    /**
     * Constructs a new FavouriteCurrencyController.
     *
     * @param interactor the input boundary for the favourite currency use case.
     */
    public FavouriteCurrencyController(FavouriteCurrencyInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Toggles or sets the favourite status for a currency.
     *
     * @param userId          the current user's ID.
     * @param currencyCode    the currency code to update.
     * @param markAsFavourite true to add to favourites, false to remove.
     */
    public void execute(String userId, String currencyCode, boolean markAsFavourite) {
        FavouriteCurrencyInputData inputData =
                new FavouriteCurrencyInputData(userId, currencyCode, markAsFavourite);
        interactor.execute(inputData);
    }
}
