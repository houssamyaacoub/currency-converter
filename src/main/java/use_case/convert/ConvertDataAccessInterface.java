package use_case.convert;
import data_access.ApiConversionResult;

public interface ConvertDataAccessInterface {

    /**
     * Retrieves conversion data (exchange rate, converted result, timestamp)
     * from a data source such as an external API or an in-memory test object.
     *
     * @param from   The base currency code (e.g., "CAD").
     * @param to     The target currency code (e.g., "USD").
     * @param amount The amount to convert.
     * @return An ApiConversionResult object containing:
     *         - exchange rate
     *         - converted result
     *         - timestamp/date
     * @throws RuntimeException if the data source request fails
     *                          or if parsing fails.
     */
    ApiConversionResult getConversion(String from, String to, double amount);
}

