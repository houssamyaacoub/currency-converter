package use_case.convert;

import entity.CurrencyConversion;
import entity.Currency;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Interface (Port) for retrieving exchange rate data.
 * This interface defines the contract that the Data Access layer (DAOs) must implement
 * to provide currency conversion data to the Interactor. It allows the Use Case layer
 * to remain independent of the specific data source (API, Database, File).
 */
public interface ExchangeRateDataAccessInterface {

    /**
     * Fetches the latest exchange rate between two currencies.
     *
     * @param from The full base {@link Currency} entity.
     * @param to   The full target {@link Currency} entity.
     * @return A {@link CurrencyConversion} entity populated with the latest rate and timestamp.
     * @throws RuntimeException if the data source request fails or the API is unreachable.
     */
    CurrencyConversion getLatestRate(Currency from, Currency to);

    /**
     * Fetches historical exchange rates between two currencies over a specified date range.
     *
     * @param from  The base currency entity.
     * @param to    The target currency entity.
     * @param start The start date of the range (inclusive).
     * @param end   The end date of the range (inclusive).
     * @return A list of {@link CurrencyConversion} entities, representing the exchange rates
     * found within the specified period.
     * @throws RuntimeException if the data retrieval fails.
     */
    List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end);
}