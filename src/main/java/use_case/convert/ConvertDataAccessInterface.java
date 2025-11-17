package use_case.convert;

import data_access.ApiConversionResult;

/**
 * Data access interface for retrieving currency conversion information.
 *
 * <p>This interface abstracts the source of exchange rate data for the
 * Convert Currency use case. Implementations may fetch data from a
 * real external API or provide in-memory test data.</p>
 */
public interface ConvertDataAccessInterface {

    /**
     * Returns conversion information including the exchange rate,
     * converted amount, and timestamp.
     *
     * @param from   the base currency code (e.g., "CAD")
     * @param to     the target currency code (e.g., "USD")
     * @param amount the amount to convert
     * @return an {@link ApiConversionResult} containing rate, result, and timestamp
     * @throws RuntimeException if the data source request fails or the response cannot be parsed
     */
    ApiConversionResult getConversion(String from, String to, double amount);
}
