package use_case.convert_multiple;

import java.util.List;

public class ConvertMultipleInputData {

    private final double amount;
    private final String fromCurrencyName;
    private final List<String> targetCurrencyNames;

    public ConvertMultipleInputData(double amount,
                                    String fromCurrencyName,
                                    List<String> targetCurrencyNames) {
        this.amount = amount;
        this.fromCurrencyName = fromCurrencyName;
        this.targetCurrencyNames = targetCurrencyNames;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromCurrencyName() {
        return fromCurrencyName;
    }

    public List<String> getTargetCurrencyNames() {
        return targetCurrencyNames;
    }
}
