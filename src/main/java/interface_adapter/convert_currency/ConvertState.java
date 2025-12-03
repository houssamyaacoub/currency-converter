package interface_adapter.convert_currency;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the Convert Currency View.
 * This class acts as a Data Transfer Object (DTO) that stores the current state of the UI,
 * including user inputs (selected currencies, amount), data for dropdowns (currency codes),
 * and the results of operations (converted amount, errors).
 * It adheres to Clean Architecture by existing in the Interface Adapter layer, allowing
 * the ViewModel to hold data without depending on specific UI elements (like Swing components).
 */
public class ConvertState {

    // Default values used on initial app launch
    private String fromCurrency = "Turkish Lira";
    private String toCurrency = "Lebanese Pound";
    private String amount = "";

    // Holds the list of currency codes/names for populating the UI ComboBoxes
    private String[] currencyCodes = new String[0];

    // Output fields for display
    private String convertedAmountResult = "0.00";
    private String rateDetails = "Rate: N/A";
    private String error = null;

    // Fields for the Multi-Compare Use Case
    private List<String> compareTargets = new ArrayList<>();
    private List<Double> compareRates = new ArrayList<>();

    /**
     * Constructs a new ConvertState with default values.
     */
    public ConvertState() {}

    /**
     * Copy constructor.
     * Creates a new state object by copying values from an existing state.
     * This is useful for updating specific fields while preserving the rest of the state.
     *
     * @param copy The state object to copy from.
     */
    public ConvertState(ConvertState copy) {
        this.fromCurrency = copy.fromCurrency;
        this.toCurrency = copy.toCurrency;
        this.amount = copy.amount;
        this.currencyCodes = copy.currencyCodes;
        this.convertedAmountResult = copy.convertedAmountResult;
        this.rateDetails = copy.rateDetails;
        this.error = copy.error;
        // Shallow copy is sufficient as lists are replaced, not modified, by the Presenter
        this.compareTargets = copy.compareTargets;
        this.compareRates = copy.compareRates;
    }

    // --- Getters ---

    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public String getAmount() { return amount; }
    public String[] getCurrencyCodes() { return currencyCodes; }
    public String getConvertedAmountResult() { return convertedAmountResult; }
    public String getRateDetails() { return rateDetails; }
    public String getError() { return error; }
    public List<String> getCompareTargets() { return compareTargets; }
    public List<Double> getCompareRates() { return compareRates; }

    // --- Setters ---

    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public void setAmount(String amount) { this.amount = amount; }

    public void setCurrencyCodes(String[] currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public void setConvertedAmountResult(String convertedAmountResult) {
        this.convertedAmountResult = convertedAmountResult;
    }

    public void setRateDetails(String rateDetails) {
        this.rateDetails = rateDetails;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCompareTargets(List<String> compareTargets) {
        this.compareTargets = compareTargets;
    }

    public void setCompareRates(List<Double> compareRates) {
        this.compareRates = compareRates;
    }
}
