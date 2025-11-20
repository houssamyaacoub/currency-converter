package interface_adapter.convert_currency;

import interface_adapter.ViewModel;


public class ConvertViewModel extends ViewModel<ConvertState> {

    public static final String TITLE_LABEL = "Currency Converter";


    public ConvertViewModel() {
        super("convert");
        // Initialize the state
        this.setState(new ConvertState());
    }

}