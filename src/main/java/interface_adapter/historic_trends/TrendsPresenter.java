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
    public void prepareSuccessView(TrendsOutputData outputData) {
        // 1. Get the current state of the Trends View
        TrendsState state = trendsViewModel.getState();

        // 2. Update the state with the data passed from the Interactor
        state.setPair(outputData.getBaseCurrency(), outputData.getTargetCurrency());

        // (Optional) If you had real graph data in OutputData, you would set it here too:
        // state.setData(outputData.getDates(), outputData.getRates());

        // 3. Update the ViewModel (this triggers the PropertyChange listener if any)
        this.trendsViewModel.setState(state);
        this.trendsViewModel.firePropertyChange();

        // 4. Switch the View!
        // We tell the manager to swap the card to "trends"
        this.viewManagerModel.setState(trendsViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView() {
        // If something went wrong, we might want to show a popup on the CURRENT screen
        // But since we are switching screens, we usually just don't switch.
        System.out.println("Error in Trends Presenter");
    }

    @Override
    public void prepareHomeView() {
        this.viewManagerModel.setState("home");
        this.viewManagerModel.firePropertyChange();
    }
}