package interface_adapter.load_currencies;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Load Currencies Use Case. Holds status/error information.
 */
public class LoadCurrenciesViewModel extends ViewModel<LoadCurrenciesState> {

    public LoadCurrenciesViewModel() {
        super("load_currencies");
        this.setState(new LoadCurrenciesState());
    }

    public void setState(LoadCurrenciesState state) {
        super.setState(state);
        firePropertyChange();
    }

    public LoadCurrenciesState getState() {
        return super.getState();
    }
}