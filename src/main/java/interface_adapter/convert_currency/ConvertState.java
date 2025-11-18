package interface_adapter.convert_currency;

// Note: Ensure this is the correct package structure for your project.

public class ConvertState {

    private String fromCurrency = "USD";
    private String toCurrency = "CAD";
    private String amount = ""; // User input amount

    // Output fields
    private String convertedAmountResult = "0.00"; // The calculated value
    private String rateDetails = "Rate: N/A";
    private String error = null;

    // Constructors and Copy Constructor (Essential for state management)
    public ConvertState() {}

    public ConvertState(ConvertState copy) {
        fromCurrency = copy.fromCurrency;
        toCurrency = copy.toCurrency;
        amount = copy.amount;
        convertedAmountResult = copy.convertedAmountResult;
        rateDetails = copy.rateDetails;
        error = copy.error;
    }

    // --- Getters ---
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public String getAmount() { return amount; }
    public String getConvertedAmountResult() { return convertedAmountResult; }
    public String getRateDetails() { return rateDetails; }
    public String getError() { return error; }

    // --- Setters (Called by View/Presenter) ---
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setConvertedAmountResult(String convertedAmountResult) { this.convertedAmountResult = convertedAmountResult; }
    public void setRateDetails(String rateDetails) { this.rateDetails = rateDetails; }
    public void setError(String error) { this.error = error; }
}