package interface_adapter.convert_currency;

import use_case.convert_multiple.ConvertMultipleOutputBoundary;
import use_case.convert_multiple.ConvertMultipleOutputData;

public class ConvertMultiplePresenter implements ConvertMultipleOutputBoundary {

    private final ConvertMultipleViewModel viewModel;

    public ConvertMultiplePresenter(ConvertMultipleViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(ConvertMultipleOutputData outputData) {
        ConvertMultipleState state = viewModel.getState();
        state.setBaseCurrencyName(outputData.getBaseCurrencyName());
        state.setAmount(outputData.getAmount());
        state.setConversions(outputData.getConversions());
        state.setFailedTargets(outputData.getFailedTargets());
        state.setError(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ConvertMultipleState state = viewModel.getState();
        state.setError(errorMessage);
        state.setConversions(new java.util.ArrayList<>());
        state.setFailedTargets(new java.util.ArrayList<>());
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
