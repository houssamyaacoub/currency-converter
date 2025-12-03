package use_case.convert;

/**
 * The Input Data for the Convert Currency Use Case.
 * This class encapsulates the input data provided by the user (via the Controller)
 * required to perform a currency conversion. It is an immutable Data Transfer Object
 * passed to the {@link ConvertInputBoundary}.
 */
public class ConvertInputData {
    final private double amount;
    final private String fromCurrency;
    final private String toCurrency;

    /**
     * Constructs a new ConvertInputData object.
     *
     * @param amount       The monetary amount to be converted.
     * @param fromCurrency The identifier (name or code) of the currency to convert from.
     * @param toCurrency   The identifier (name or code) of the currency to convert to.
     */
    public ConvertInputData(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    /**
     * Gets the amount to convert.
     *
     * @return the amount as a double.
     */
    public double getAmount() { return amount; }

    /**
     * Gets the source currency.
     *
     * @return the name or code of the source currency.
     */
    public String getFromCurrency() { return fromCurrency; }

    /**
     * Gets the target currency.
     *
     * @return the name or code of the target currency.
     */
    public String getToCurrency() { return toCurrency; }
}