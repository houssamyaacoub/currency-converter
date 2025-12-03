package entity;

import java.time.Instant;

/**
 * Entity representing a specific currency conversion result.
 * This class encapsulates the data related to a conversion between two currencies
 * at a specific point in time, including the exchange rate used.
 * It also contains the core business logic for calculating the converted amount.
 * As an Entity, this class resides in the innermost layer of Clean Architecture
 * and has no dependencies on outer layers (like Database or UI).
 */
public class CurrencyConversion {
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private final double rate;
    private final Instant timestamp;

    /**
     * Constructs a new CurrencyConversion entity.
     *
     * @param fromCurrency The base currency being converted from.
     * @param toCurrency   The target currency being converted to.
     * @param rate         The exchange rate used (1 unit of 'from' = 'rate' units of 'to').
     * @param timestamp    The exact time this rate was valid.
     */
    public CurrencyConversion(Currency fromCurrency, Currency toCurrency, double rate, Instant timestamp) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    /**
     * Core Business Logic: Calculates the converted amount based on the stored rate.
     *
     * @param amount The amount of the base currency to convert.
     * @return The equivalent amount in the target currency.
     */
    public double calculateConvertedAmount(double amount) {
        return amount * rate;
    }

    /**
     * Gets the base currency.
     *
     * @return The source Currency object.
     */
    public Currency getFromCurrency() {
        return fromCurrency;
    }

    /**
     * Gets the target currency.
     *
     * @return The target Currency object.
     */
    public Currency getToCurrency() {
        return toCurrency;
    }

    /**
     * Gets the exchange rate.
     *
     * @return The rate used for conversion.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Gets the timestamp of the conversion rate.
     *
     * @return The time the rate was valid.
     */
    public Instant getTimeStamp() {
        return timestamp;
    }
}