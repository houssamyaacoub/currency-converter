package interface_adapter.favourite_currency;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Favourite Currency feature (Use Case 5).
 *
 * The view listens to this ViewModel for property changes and updates the UI
 * whenever the FavouriteCurrencyState changes.
 */
public class FavouriteCurrencyViewModel extends ViewModel<FavouriteCurrencyState> {

    public static final String VIEW_NAME = "favourite-currency";

    public FavouriteCurrencyViewModel() {
        super(VIEW_NAME);
        setState(new FavouriteCurrencyState());
    }
}
