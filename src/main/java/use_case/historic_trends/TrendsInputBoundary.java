package use_case.historic_trends;

/**
 * Input Boundary interface for the Historical Trends Use Case.
 * <p>
 * This interface defines the contract for the Controller to interact with the Use Case Interactor.
 * It serves as the entry point into the application's business logic layer (Pink Box), adhering to the
 * Dependency Inversion Principle by decoupling the Controller from the concrete Interactor implementation.
 */
public interface TrendsInputBoundary {

    /**
     * Executes the historical trends business logic.
     * <p>
     * This method takes the user input (currencies and time period), coordinates the data fetching,
     * and prepares the output for the presenter.
     *
     * @param trendsInputData the data transfer object containing the user's input parameters
     * (base currency, target currency, and selected time period).
     */
    void execute(TrendsInputData trendsInputData);

    /**
     * Initiates the workflow to navigate back to the Home View.
     * <p>
     * This method allows the Controller to trigger navigation logic within the Interactor,
     * which then delegates the actual view switching to the Presenter.
     */
    void switchToHomeView();

    void executeInitialLoad();

}