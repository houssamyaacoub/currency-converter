package interface_adapter.historic_trends;

import interface_adapter.ViewModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
public class TrendsViewModel extends ViewModel<TrendsState> {

    public TrendsViewModel() {
        super("trends");
        setState(new TrendsState());
    }
}
