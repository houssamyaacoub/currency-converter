package use_case.favourite_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the Favourite Currency use case (Use Case 5).
 *
 * Application business rules for managing a user's favourite currencies.
 *
 * Behaviour:
 *   - Each call to execute(...) TOGGLES the given currency:
 *       * if the currency is not in favourites -> it is added
 *       * if the currency is already in favourites -> it is removed
 */
public class FavouriteCurrencyInteractor implements FavouriteCurrencyInputBoundary {

    private final FavouriteCurrencyDataAccessInterface favouriteGateway;
    private final FavouriteCurrencyOutputBoundary presenter;

    /**
     * Constructs a new FavouriteCurrencyInteractor.
     *
     * @param favouriteGateway the gateway used to access favourite data.
     * @param presenter        the output boundary used to present results.
     */
    public FavouriteCurrencyInteractor(FavouriteCurrencyDataAccessInterface favouriteGateway,
                                       FavouriteCurrencyOutputBoundary presenter) {
        this.favouriteGateway = favouriteGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(FavouriteCurrencyInputData inputData) {
        String userId = inputData.getUserId();
        String code = inputData.getCurrencyCode();

        // ----- basic validation -----
        if (userId == null || userId.isEmpty()) {
            presenter.prepareFailView("User is not logged in.");
            return;
        }
        if (!favouriteGateway.userExists(userId)) {
            presenter.prepareFailView("User does not exist.");
            return;
        }
        if (code == null || code.trim().isEmpty()) {
            presenter.prepareFailView("Currency code is empty.");
            return;
        }

        // We keep the original case for display; just trim whitespace.
        String normalized = code.trim();

        // ----- read current favourites from gateway -----
        List<String> favourites = new ArrayList<>(
                favouriteGateway.getFavouritesForUser(userId));

        // ----- TOGGLE logic -----
        if (favourites.contains(normalized)) {
            // already a favourite -> remove it
            favourites.remove(normalized);
        } else {
            // not yet in favourites -> add it
            favourites.add(normalized);
        }

        // ----- persist updated list -----
        favouriteGateway.saveFavouritesForUser(userId, favourites);

        // ----- prepare output -----
        FavouriteCurrencyOutputData outputData =
                new FavouriteCurrencyOutputData(userId, favourites);

        presenter.prepareSuccessView(outputData);
    }
}

