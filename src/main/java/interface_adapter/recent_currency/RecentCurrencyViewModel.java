package interface_adapter.recent_currency;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Recent / Frequently Used Currencies feature (Use Case 8).
 * The view listens to this ViewModel to update any UI elements that depend on
 * the ordered list of currencies.
 */
public class RecentCurrencyViewModel extends ViewModel<RecentCurrencyState> {

    /**
     * Logical name of this view; must match the name used in the view-switching
     * mechanism (for example, in controllers or app builder).
     */

    public static final String VIEW_NAME = "recent-currency";

    /**
     * Creates a new RecentCurrencyViewModel with an empty default state.
     */

    public RecentCurrencyViewModel() {
        super(VIEW_NAME);
        setState(new RecentCurrencyState());
    }
}
