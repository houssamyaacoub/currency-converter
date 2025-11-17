package use_case.convert;

public class ConvertInputData {
    final private double amount;
    final private String fromCurrency;
    final private String toCurrency;

    public ConvertInputData(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public double getAmount() { return amount; }
    public String getFromCurrency() { return fromCurrency; }
    public String getToCurrency() { return toCurrency; }
}