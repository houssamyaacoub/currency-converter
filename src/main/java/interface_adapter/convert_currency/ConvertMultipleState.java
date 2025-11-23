package interface_adapter.convert_currency;

import use_case.convert_multiple.ConvertMultipleOutputData;

import java.util.ArrayList;
import java.util.List;

public class ConvertMultipleState {

    private String baseCurrencyName;
    private double amount;
    private List<ConvertMultipleOutputData.ConversionResult> conversions;
    private List<String> failedTargets;
    private String error;

    public ConvertMultipleState() {
        this.conversions = new ArrayList<>();
        this.failedTargets = new ArrayList<>();
    }

    public ConvertMultipleState(ConvertMultipleState copy) {
        this.baseCurrencyName = copy.baseCurrencyName;
        this.amount = copy.amount;
        this.conversions = new ArrayList<>(copy.conversions);
        this.failedTargets = new ArrayList<>(copy.failedTargets);
        this.error = copy.error;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<ConvertMultipleOutputData.ConversionResult> getConversions() {
        return conversions;
    }

    public void setConversions(List<ConvertMultipleOutputData.ConversionResult> conversions) {
        this.conversions = conversions;
    }

    public List<String> getFailedTargets() {
        return failedTargets;
    }

    public void setFailedTargets(List<String> failedTargets) {
        this.failedTargets = failedTargets;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
