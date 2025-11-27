package use_case.favourite_currency;

/**
 * Output boundary for the Favourite Currency use case (Use Case 5).
 *
 * The interactor calls this interface to present a success or failure result
 * to the presenter, which then updates the ViewModel.
 */
public interface FavouriteCurrencyOutputBoundary {

    /**
     * Called when the favourite currency use case completes successfully.
     *
     * @param outputData information needed to update the view, including
     *                   the updated list of favourite currencies.
     */
    void prepareSuccessView(FavouriteCurrencyOutputData outputData);

    /**
     * Called when the favourite currency use case fails.
     *
     * @param errorMessage a user-facing error message that can be displayed.
     */
    void prepareFailView(String errorMessage);
}
