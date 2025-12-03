package use_case.recent_currency;

/**
 * Output boundary for the Recent / Frequently Used Currencies use case (Use Case 8).
 * The interactor calls this interface to present the updated ordering of currencies
 * so that the presenter can update the ViewModel.
 */
public interface RecentCurrencyOutputBoundary {

    /**
     * Called when the recent currency use case completes successfully.
     *
     * @param outputData information needed to update the view, including
     *                   favourites, top frequent currencies, and the
     *                   final ordered list for dropdowns.
     */
    void prepareSuccessView(RecentCurrencyOutputData outputData);

    /**
     * Called when the recent currency use case fails.
     *
     * @param errorMessage a user-facing error message that can be displayed.
     */
    void prepareFailView(String errorMessage);
}
