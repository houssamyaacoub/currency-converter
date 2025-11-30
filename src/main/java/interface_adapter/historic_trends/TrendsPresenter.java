package interface_adapter.historic_trends;

import interface_adapter.ViewManagerModel;
import use_case.historic_trends.TrendsOutputBoundary;
import use_case.historic_trends.TrendsOutputData;

public class TrendsPresenter implements TrendsOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final TrendsViewModel trendsViewModel;

    public TrendsPresenter(ViewManagerModel viewManagerModel,
                           TrendsViewModel trendsViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.trendsViewModel = trendsViewModel;
    }

    @Override
    public void prepareSuccessView(TrendsOutputData data) {
        TrendsState state = trendsViewModel.getState();
        state.setBaseCurrency(data.getBaseCurrency());
        state.setSeriesList(data.getSeriesList());
        trendsViewModel.setState(state);
        trendsViewModel.firePropertyChange();

        viewManagerModel.setActiveView("trends");
        viewManagerModel.firePropertyChange();
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
    public void prepareFailView(String errorMessage) {
        TrendsState state = trendsViewModel.getState();
        state.setError(errorMessage);
        trendsViewModel.setState(state);
        trendsViewModel.firePropertyChange();
    }

    @Override
    public void prepareHomeView() {
        viewManagerModel.setActiveView("home");
        viewManagerModel.firePropertyChange();
    }
}
