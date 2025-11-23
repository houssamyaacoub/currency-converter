package interface_adapter.convert_currency;

import interface_adapter.ViewModel;

public class ConvertMultipleViewModel extends ViewModel<ConvertMultipleState> {

    public static final String TITLE_LABEL = "Compare Multiple Currencies";

    public ConvertMultipleViewModel() {
        super("compare");
        this.setState(new ConvertMultipleState());
    }
}
