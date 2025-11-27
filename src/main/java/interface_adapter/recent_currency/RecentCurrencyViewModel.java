package interface_adapter.recent_currency;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Recent / Frequently Used Currencies feature (Use Case 8).
 *
 * The view listens to this ViewModel to update any UI elements that depend on
 * the ordered list of currencies.
 */
public class RecentCurrencyViewModel extends ViewModel<RecentCurrencyState> {

    public static final String VIEW_NAME = "recent-currency";

    public RecentCurrencyViewModel() {
        super(VIEW_NAME);
        setState(new RecentCurrencyState());
    }
}
