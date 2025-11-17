package interface_adapter.logged_in;

import interface_adapter.ViewModel;

/**
 * The View Model for the Logged In View.
 */
public class HomeViewModel extends ViewModel<HomeState> {

    public HomeViewModel() {
        super("home");
        setState(new HomeState());
    }

}
