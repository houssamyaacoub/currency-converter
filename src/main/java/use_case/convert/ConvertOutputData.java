package use_case.convert;

import java.time.Instant;

/**
 * Data structure carrying the result or error from the Interactor to the Presenter.
 */
public class ConvertOutputData {

    // Success fields
    private final double convertedAmount;
    private final double rate;
    private final String targetCurrencyCode;
    private final Instant timestamp;

    // --- CONSTRUCTOR FOR SUCCESS ---
    public ConvertOutputData(double convertedAmount, double rate, String targetCurrencyCode, Instant timestamp) {
        this.convertedAmount = convertedAmount;
        this.rate = rate;
        this.targetCurrencyCode = targetCurrencyCode;
        this.timestamp = timestamp;
    }

    // --- Getters (Required by the Presenter) ---
    public double getConvertedAmount() {
        return convertedAmount;
    }

    public double getRate() {
        return rate;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}