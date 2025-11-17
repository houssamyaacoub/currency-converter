package data_access;

public class ApiConversionResult {
    private final double rate;
    private final double result;
    private final String timestamp; // API provides this as "date"

    public ApiConversionResult(double rate, double result, String timestamp) {
        this.rate = rate;
        this.result = result;
        this.timestamp = timestamp;
    }

    public double getRate() {
        return rate;
    }

    public double getResult() {
        return result;
    }

    public String getTimestamp() {
        return timestamp;
    }
}