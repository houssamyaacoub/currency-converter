package use_case.travel_budget;

public interface TravelBudgetInputBoundary {

    void execute(TravelBudgetInputData inputData);

    void switchToHomeView();
}
