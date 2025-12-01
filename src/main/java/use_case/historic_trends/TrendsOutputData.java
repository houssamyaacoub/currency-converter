package use_case.historic_trends;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Output Data for the Historical Trends Use Case.
 * <p>
 * This class serves as an immutable Data Transfer Object (DTO) that encapsulates the results
 * of the historical trends analysis. It is created by the Interactor after successfully
 * fetching and processing data, and is then passed to the Presenter.
 * <p>
 * It contains the aligned lists of dates and rates required to generate the
 * visualization (graph) in the View.
 */
public class TrendsOutputData {

    private final String baseCurrency;
    private final String targetCurrency;
    private final ArrayList<LocalDate> dates;
    private final ArrayList<Double> rates;
    private final boolean useCaseFailed;

    /**
     * Constructs a new TrendsOutputData object.
     *
     * @param baseCurrency   The identifier of the base currency (e.g., "USD").
     * @param targetCurrency The identifier of the target currency (e.g., "EUR").
     * @param dates          The list of dates corresponding to the historical rates (X-axis data).
     * @param rates          The list of exchange rates corresponding to the dates (Y-axis data).
     * @param useCaseFailed  A flag indicating whether the use case execution failed (true) or succeeded (false).
     */
    public TrendsOutputData(String baseCurrency, String targetCurrency,
                            ArrayList<LocalDate> dates, ArrayList<Double> rates,
                            boolean useCaseFailed) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.dates = dates;
        this.rates = rates;
        this.useCaseFailed = useCaseFailed;
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
     * Gets the list of dates for the historical data points.
     *
     * @return an ArrayList of LocalDate objects representing the time series X-axis.
     */
    public ArrayList<LocalDate> getDates() {
        return dates;
    }

    /**
     * Gets the list of exchange rates for the historical data points.
     *
     * @return an ArrayList of Double values representing the exchange rates (Y-axis).
     */
    public ArrayList<Double> getRates() {
        return rates;
    }

    /**
     * Checks if the use case failed.
     * * @return true if the use case failed, false otherwise.
     */
    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}