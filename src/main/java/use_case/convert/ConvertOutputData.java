package use_case.convert;

import java.time.Instant;

/**
 * Output Data for the Convert Currency Use Case.
 * This Data Transfer Object (DTO) encapsulates the results of a successful currency conversion,
 * passing data from the {@link ConvertCurrencyInteractor} to the {@link ConvertOutputBoundary} (Presenter).
 * It is immutable to ensure data integrity as it crosses the architectural boundary.
 */
public class ConvertOutputData {

    private final double convertedAmount;
    private final double rate;
    private final String targetCurrencyCode;
    private final Instant timestamp;

    /**
     * Constructs a new ConvertOutputData instance.
     *
     * @param convertedAmount    The final calculated amount in the target currency.
     * @param rate               The exchange rate used for the conversion.
     * @param targetCurrencyCode The ISO code of the target currency (e.g., "EUR").
     * @param timestamp          The time at which the exchange rate was valid.
     */
    public ConvertOutputData(double convertedAmount, double rate, String targetCurrencyCode, Instant timestamp) {
        this.convertedAmount = convertedAmount;
        this.rate = rate;
        this.targetCurrencyCode = targetCurrencyCode;
        this.timestamp = timestamp;
    }

    /**
     * Gets the converted amount.
     *
     * @return The converted amount as a double.
     */
    public double getConvertedAmount() {
        return convertedAmount;
    }

    /**
     * Gets the exchange rate used.
     *
     * @return The exchange rate.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Gets the target currency code.
     *
     * @return The 3-letter ISO currency code (e.g., "USD").
     */
    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    /**
     * Gets the timestamp of the conversion rate.
     *
     * @return The timestamp as an Instant.
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}