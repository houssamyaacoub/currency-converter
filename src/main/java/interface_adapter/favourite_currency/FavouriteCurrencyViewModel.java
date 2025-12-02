package interface_adapter.favourite_currency;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Favourite Currency feature (Use Case 5).
 * The view listens to this ViewModel for property changes and updates the UI
 * whenever the FavouriteCurrencyState changes.
 */
public class FavouriteCurrencyViewModel extends ViewModel<FavouriteCurrencyState> {

    /**
     * Logical name of this view; must match the name used in the
     * view-switching mechanism (e.g., in controllers or app builder).
     */

    public static final String VIEW_NAME = "favourite-currency";

    /**
     * Creates a new FavouriteCurrencyViewModel with an empty default state.
     */

    public FavouriteCurrencyViewModel() {
        super(VIEW_NAME);
        setState(new FavouriteCurrencyState());
    }
}
