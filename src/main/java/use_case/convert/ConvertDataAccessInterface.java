package use_case.convert;
import data_access.ApiConversionResult;

public interface ConvertDataAccessInterface {

    /**
     * Fetches the conversion result from the external API.
     *
     * @param from   The base currency code (e.g., "CAD").
     * @param to     The target currency code (e.g., "USD").
     * @param amount The amount to convert.
     * @return An ApiConversionResult object containing the rate, result, and timestamp.
     * @throws RuntimeException if the API call fails or JSON parsing fails.
     */
    ApiConversionResult getConversion(String from, String to, double amount);
}

