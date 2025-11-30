package interface_adapter.convert_currency;

import java.util.ArrayList;
import java.util.List;

public class ConvertState {

    private String fromCurrency = "Turkish Lira"; // Default value
    private String toCurrency = "Lebanese Pound"; // Default value
    private String amount = "";

    // Holds the list of currency codes/names for the ComboBoxes
    private String[] currencyCodes = new String[]{};

    // Output fields
    private String convertedAmountResult = "0.00";
    private String rateDetails = "Rate: N/A";
    private String error = null;

    // NEW: data for multi-currency compare
    private List<String> compareTargets = new ArrayList<>();
    private List<Double> compareRates = new ArrayList<>();

    public ConvertState() {}

    public ConvertState(ConvertState copy) {
        this.fromCurrency = copy.fromCurrency;
        this.toCurrency = copy.toCurrency;
        this.amount = copy.amount;
        this.currencyCodes = copy.currencyCodes;
        this.convertedAmountResult = copy.convertedAmountResult;
        this.rateDetails = copy.rateDetails;
        this.error = copy.error;
        this.compareTargets = copy.compareTargets;
        this.compareRates = copy.compareRates;
    }

    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public String getAmount() { return amount; }
    public String[] getCurrencyCodes() { return currencyCodes; }
    public String getConvertedAmountResult() { return convertedAmountResult; }
    public String getRateDetails() { return rateDetails; }
    public String getError() { return error; }

    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public void setAmount(String amount) { this.amount = amount; }

    public void setCurrencyCodes(String[] currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public void setConvertedAmountResult(String convertedAmountResult) { this.convertedAmountResult = convertedAmountResult; }
    public void setRateDetails(String rateDetails) { this.rateDetails = rateDetails; }
    public void setError(String error) { this.error = error; }

    // === NEW for multi compare ===

    public List<String> getCompareTargets() {
        return compareTargets;
    }

    public void setCompareTargets(List<String> compareTargets) {
        this.compareTargets = compareTargets;
    }

    public List<Double> getCompareRates() {
        return compareRates;
    }

    public void setCompareRates(List<Double> compareRates) {
        this.compareRates = compareRates;
    }
}
