package use_case.travel_budget;

/**
 * Input boundary for the Travel Budget use case.
 * <br>
 * Implemented by the interactor. Called by the controller.
 */
public interface TravelBudgetInputBoundary {

    /**
     * Executes the travel budget calculation for a given set of inputs.
     *
     * @param inputData all data required to compute the travel budget
     */
    void execute(TravelBudgetInputData inputData);

    /**
     * Requests that the application return to the home view.
     */
    void switchToHomeView();
}
