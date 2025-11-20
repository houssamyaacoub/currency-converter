package interface_adapter.convert_currency;

public class ConvertState {

    private String fromCurrency = "Turkish Lira"; // Default value
    private String toCurrency = "Lebanese Pound"; // Default value
    private String amount = "";

    // NEW: Holds the list of currency codes/names for the ComboBoxes
    private String[] currencyCodes = new String[]{};

    // Output fields
    private String convertedAmountResult = "0.00";
    private String rateDetails = "Rate: N/A";
    private String error = null;

    // --- Constructors ---

    public ConvertState() {}

    // Copy Constructor (Required for preserving state while updating)
    public ConvertState(ConvertState copy) {
        this.fromCurrency = copy.fromCurrency;
        this.toCurrency = copy.toCurrency;
        this.amount = copy.amount;
        this.currencyCodes = copy.currencyCodes; // Copy the list
        this.convertedAmountResult = copy.convertedAmountResult;
        this.rateDetails = copy.rateDetails;
        this.error = copy.error;
    }

    // --- Getters ---
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public String getAmount() { return amount; }
    public String[] getCurrencyCodes() { return currencyCodes; } // Getter for the list
    public String getConvertedAmountResult() { return convertedAmountResult; }
    public String getRateDetails() { return rateDetails; }
    public String getError() { return error; }

    // --- Setters ---
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public void setAmount(String amount) { this.amount = amount; }

    // Setter for the list (Called by LoadCurrenciesPresenter/AppBuilder)
    public void setCurrencyCodes(String[] currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public void setConvertedAmountResult(String convertedAmountResult) { this.convertedAmountResult = convertedAmountResult; }
    public void setRateDetails(String rateDetails) { this.rateDetails = rateDetails; }
    public void setError(String error) { this.error = error; }
}