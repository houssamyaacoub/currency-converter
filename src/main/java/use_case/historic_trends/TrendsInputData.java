package use_case.historic_trends;

/**
 * Input Data for the Historical Trends Use Case.
 * <p>
 * This class encapsulates the necessary information required to retrieve historical exchange rate data.
 * It serves as an immutable Data Transfer Object (DTO) that passes user input from the
 * Controller (Interface Adapter layer) to the Interactor (Use Case layer).
 */
public class TrendsInputData {

    private final String baseCurrency;
    private final String targetCurrency;
    private final String timePeriod;

    /**
     * Constructs a new TrendsInputData object.
     *
     * @param baseCurrency   The identifier (name or code) of the base currency (e.g., "United States Dollar" or "USD").
     * @param targetCurrency The identifier (name or code) of the target currency (e.g., "Euro" or "EUR").
     * @param timePeriod     The duration for the historical analysis (e.g., "1 week", "1 month", "1 year").
     */
    public TrendsInputData(String baseCurrency, String targetCurrency, String timePeriod) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.timePeriod = timePeriod;
    }

    /**
     * Gets the base currency identifier.
     *
     * @return the string representing the base currency.
     */
    public String getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * Gets the target currency identifier.
     *
     * @return the string representing the target currency.
     */
    public String getTargetCurrency() {
        return targetCurrency;
    }

    /**
     * Gets the selected time period for the trend analysis.
     *
     * @return the string representing the time period.
     */
    public String getTimePeriod() {
        return timePeriod;
    }
}