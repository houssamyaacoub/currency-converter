package interface_adapter.travel_budget;

import interface_adapter.ViewManagerModel;
import use_case.travel_budget.TravelBudgetOutputBoundary;
import use_case.travel_budget.TravelBudgetOutputData;

import java.util.Locale;

public class TravelBudgetPresenter implements TravelBudgetOutputBoundary {

    private final TravelBudgetViewModel travelBudgetViewModel;
    private final ViewManagerModel viewManagerModel;

    public TravelBudgetPresenter(ViewManagerModel viewManagerModel,
                                 TravelBudgetViewModel travelBudgetViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.travelBudgetViewModel = travelBudgetViewModel;
    }

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

    @Override
    public void prepareFailView(String errorMessage) {
        TravelBudgetState state = travelBudgetViewModel.getState();
        state.setError(errorMessage);

        travelBudgetViewModel.setState(state);
        travelBudgetViewModel.firePropertyChange();
    }

    @Override
    public void prepareHomeView() {
        viewManagerModel.setState("home");
        viewManagerModel.firePropertyChange();
    }
}
