package use_case.convert;

import entity.CurrencyConversion;
import entity.Currency;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Data access interface (Port) for retrieving currency conversion information.
 */
public interface ExchangeRateDataAccessInterface {

    /**
     * Fetches the exchange rate based on the full Currency entities.
     *
     * @param from   the full base {@link Currency} entity
     * @param to     the full target {@link Currency} entity
     * @return a {@link CurrencyConversion} Entity populated with the latest rate and timestamp.
     * @throws RuntimeException if the data source request fails.
     */
    CurrencyConversion getLatestRate(Currency from, Currency to);
    List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end);
}