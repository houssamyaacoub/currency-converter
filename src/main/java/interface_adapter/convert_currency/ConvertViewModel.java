package interface_adapter.convert_currency;

import interface_adapter.ViewModel; // Now extends the provided base class


public class ConvertViewModel extends ViewModel<ConvertState> {

    public static final String TITLE_LABEL = "Currency Converter";

    // State is now managed by the base class (ViewModel<T>).

    public ConvertViewModel() {
        super("home"); // Assuming "home" is the viewName for HomeView
        // Initialize the state
        this.setState(new ConvertState());
    }

    // --- Accessors ---

    // Getter now uses the inherited getState()
    // Setter now uses the inherited setState(state) and firePropertyChange()
    public void setState(ConvertState state) {
        super.setState(state);
        // Important: firePropertyChange() is required to notify the view.
        // We call it explicitly after setting the new state.
        firePropertyChange();
    }

    public ConvertState getState() {
        return super.getState();
    }
}