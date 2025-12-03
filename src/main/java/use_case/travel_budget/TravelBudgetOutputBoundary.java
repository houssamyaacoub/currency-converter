package use_case.travel_budget;


/**
 * Output boundary for the Travel Budget use case.
 * <br>
 * Implemented by the presenter. Called by the interactor.
 */
public interface TravelBudgetOutputBoundary {

    /**
     * Prepares the success view when the travel budget calculation finishes
     * without errors.
     *
     * @param data all data that the view needs to display the result
     */
    void prepareSuccessView(TravelBudgetOutputData data);

    /**
     * Prepares an error view when the travel budget calculation fails.
     *
     * @param errorMessage message describing what went wrong
     */
    void prepareFailView(String errorMessage);

    /**
     * Requests that the presenter switch back to the home view.
     */
    void prepareHomeView();
}
