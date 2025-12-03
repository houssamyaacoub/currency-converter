package interface_adapter.load_currencies;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Load Currencies Use Case.
 * This class holds the state of the currency loading process (e.g., loading status, error messages).
 * It extends the generic {@link ViewModel} to manage {@link LoadCurrenciesState}.
 */
public class LoadCurrenciesViewModel extends ViewModel<LoadCurrenciesState> {

    /**
     * Constructs a new LoadCurrenciesViewModel.
     * Sets the view name to "load_currencies" and initializes the state
     * with default values.
     */
    public LoadCurrenciesViewModel() {
        super("load_currencies");
        this.setState(new LoadCurrenciesState());
    }

    /**
     * Updates the state of the ViewModel and notifies observers.
     * This override ensures that every time the state is updated (e.g., loading finishes
     * or fails), the {@code firePropertyChange()} method is automatically called to
     * alert the View.
     *
     * @param state The new {@link LoadCurrenciesState}.
     */
    @Override
    public void setState(LoadCurrenciesState state) {
        super.setState(state);
        firePropertyChange();
    }
}