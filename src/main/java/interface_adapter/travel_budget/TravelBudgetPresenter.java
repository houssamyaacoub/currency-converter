package interface_adapter.travel_budget;

import interface_adapter.ViewManagerModel;
import use_case.travel_budget.TravelBudgetOutputBoundary;
import use_case.travel_budget.TravelBudgetOutputData;

import java.util.Locale;

/**
 * Presenter for the Travel Budget use case.
 *
 * <p>This class receives output data from the interactor and prepares it
 * for display in the {@link TravelBudgetViewModel}. It is responsible for:
 * <ul>
 *   <li>Formatting numbers and strings for UI display</li>
 *   <li>Updating the view model state</li>
 *   <li>Triggering screen changes via {@link ViewManagerModel}</li>
 * </ul></p>
 *
 * <p>The presenter contains no business logic — only UI preparation logic.</p>
 */
public class TravelBudgetPresenter implements TravelBudgetOutputBoundary {

    private final TravelBudgetViewModel travelBudgetViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Creates a presenter for the Travel Budget use case.
     *
     * @param viewManagerModel global view manager for switching screens
     * @param travelBudgetViewModel the view model associated with the Travel Budget view
     */
    public TravelBudgetPresenter(ViewManagerModel viewManagerModel,
                                 TravelBudgetViewModel travelBudgetViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.travelBudgetViewModel = travelBudgetViewModel;
    }

    /**
     * Prepares the success state for the UI.
     *
     * @param outputData the processed results from the interactor
     */
    @Override
    public void prepareSuccessView(TravelBudgetOutputData outputData) {
        TravelBudgetState state = travelBudgetViewModel.getState();

        state.setHomeCurrency(outputData.getHomeCurrency());
        state.setLineItems(outputData.getLineItems());


        String formatted = String.format(Locale.US, "%.2f %s",
                outputData.getTotalInHomeCurrency(),
                outputData.getHomeCurrency());

        state.setTotalFormatted(formatted);
        state.setError(null);

        travelBudgetViewModel.setState(state);
        travelBudgetViewModel.firePropertyChange();

        viewManagerModel.setState(travelBudgetViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    /**
     * Prepares an error state to show in the UI.
     *
     * @param errorMessage human-readable error message
     */
    @Override
    public void prepareFailView(String errorMessage) {
        TravelBudgetState state = travelBudgetViewModel.getState();
        state.setError(errorMessage);

        travelBudgetViewModel.setState(state);
        travelBudgetViewModel.firePropertyChange();
    }

    /**
     * Called when the user returns to the Home view.
     */
    @Override
    public void prepareHomeView() {
        viewManagerModel.setState("home");
        viewManagerModel.firePropertyChange();
    }
}
