package interface_adapter.travel_budget;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Travel Budget screen.
 * Stores and exposes the {@link TravelBudgetState} used by the view.
 * The presenter updates this state, and the view listens for changes.</p>
 */
public class TravelBudgetViewModel extends ViewModel<TravelBudgetState> {

    /**
     * Creates the view model and initializes its default state.
     */
    public TravelBudgetViewModel() {
        super("travel_budget");
        setState(new TravelBudgetState());
    }
}
