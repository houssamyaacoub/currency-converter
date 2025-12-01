package use_case.historic_trends;

/**
 * Output Boundary interface for the Historical Trends Use Case.
 * <p>
 * This interface defines the contract for the Interactor to communicate results back to the
 * Interface Adapter layer (specifically the Presenter). By defining this interface in the Use Case layer,
 * we adhere to the Dependency Inversion Principle, ensuring the business logic does not depend directly
 * on the UI or Presenter implementation.
 */
public interface TrendsOutputBoundary {

    /**
     * Prepares the view for a successful execution of the historical trends use case.
     * <p>
     * This method is called when the Interactor has successfully retrieved and processed
     * the historical data. The implementation (Presenter) should format this data
     * and update the ViewModel/State.
     *
     * @param data the output data object containing the requested historical exchange rates and dates.
     */
    void prepareSuccessView(TrendsOutputData data);

    /**
     * Prepares the view when the use case execution fails.
     * <p>
     * This method is called when an error occurs during data retrieval or processing
     * (e.g., API connection failure, invalid inputs). The implementation should
     * update the ViewModel to reflect the error state.
     *
     * @param errorMessage a description of the error to be displayed to the user.
     */
    void prepareFailView(String errorMessage);

    /**
     * Prepares the view to switch back to the Home/Hub screen.
     * <p>
     * This method allows the Interactor to trigger a navigation event, instructing the
     * Presenter (and subsequently the ViewManager) to change the active view.
     */
    void prepareHomeView();
}