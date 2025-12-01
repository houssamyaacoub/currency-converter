package interface_adapter.travel_budget;

import interface_adapter.ViewModel;

public class TravelBudgetViewModel extends ViewModel<TravelBudgetState> {

    public TravelBudgetViewModel() {
        super("travel_budget");
        setState(new TravelBudgetState());
    }
}
