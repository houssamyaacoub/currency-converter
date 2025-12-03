package interface_adapter.convert_currency;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Convert Currency View.
 * This class acts as a container for the state of the conversion screen.
 * It adheres to Clean Architecture by residing in the Interface Adapter layer,
 * holding the data (State) required by the View without depending on specific UI elements.
 */
public class ConvertViewModel extends ViewModel<ConvertState> {

    /**
     * Constructs a new ConvertViewModel.
     * Sets the view name to "convert" (used by the ViewManager) and
     * initializes the state to a default {@link ConvertState} instance.
     */
    public ConvertViewModel() {
        super("convert");
        this.setState(new ConvertState());
    }
}