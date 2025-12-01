package interface_adapter.historic_trends;

import interface_adapter.ViewManagerModel;
import interface_adapter.historic_trends.TrendsState;
import interface_adapter.historic_trends.TrendsViewModel;
import use_case.historic_trends.TrendsOutputBoundary;
import use_case.historic_trends.TrendsOutputData;

public class TrendsPresenter implements TrendsOutputBoundary {

    private final TrendsViewModel trendsViewModel;
    private final ViewManagerModel viewManagerModel;

    public TrendsPresenter(ViewManagerModel viewManagerModel,
                           TrendsViewModel trendsViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.trendsViewModel = trendsViewModel;
    }

    @Override
    public void prepareInitialView() {
        TrendsState state = trendsViewModel.getState();
        state.setError(null);

        state.setSeriesList(new java.util.ArrayList<>());

        trendsViewModel.setState(state);
        trendsViewModel.firePropertyChange();

        viewManagerModel.setActiveView("trends");
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareSuccessView(TrendsOutputData outputData) {
        // 1. Get the current state of the Trends View
        TrendsState state = trendsViewModel.getState();

        // 2. Update the state with the data passed from the Interactor
        state.setPair(outputData.getBaseCurrency(), outputData.getTargetCurrency());

        // state.setData(outputData.getDates(), outputData.getRates());
        if (outputData.getDates() != null && outputData.getRates() != null) {
            state.setData(outputData.getDates(), outputData.getRates());
        }

        // 3. Update the ViewModel (this triggers the PropertyChange listener if any)
        this.trendsViewModel.setState(state);
        this.trendsViewModel.firePropertyChange();

        // We tell the manager to swap the card to "trends"
        this.viewManagerModel.setState(trendsViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        TrendsState state = trendsViewModel.getState();
        state.setError(errorMessage);
        trendsViewModel.setState(state);
        trendsViewModel.firePropertyChange();
    }

    @Override
    public void prepareHomeView() {
        this.viewManagerModel.setState("home");
        this.viewManagerModel.firePropertyChange();
    }
}