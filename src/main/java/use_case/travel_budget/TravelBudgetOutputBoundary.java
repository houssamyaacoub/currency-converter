package use_case.travel_budget;

public interface TravelBudgetOutputBoundary {

    void prepareSuccessView(TravelBudgetOutputData data);

    void prepareFailView(String errorMessage);

    void prepareHomeView();
}
