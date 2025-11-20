package use_case.convert;

public class ConvertOutputData {

    private final double inputAmount;
    private final String fromCurrency;
    private final String toCurrency;
    private final double rate;
    private final double convertedAmount;
    private final String timestamp;

    public ConvertOutputData(double inputAmount,
                             String fromCurrency,
                             String toCurrency,
                             double rate,
                             double convertedAmount,
                             String timestamp) {
        this.inputAmount = inputAmount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
        this.timestamp = timestamp;
    }

    public double getInputAmount() {
        return inputAmount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getRate() {
        return rate;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
