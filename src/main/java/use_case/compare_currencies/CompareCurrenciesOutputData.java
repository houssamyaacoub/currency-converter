package use_case.compare_currencies;

import java.util.List;

/**
 * Data that the presenter (and eventually the UI) needs in order
 * to show the comparison chart.
 * <p>
 * It stores:
 * <ul>
 *     <li>The base currency name.</li>
 *     <li>The list of target currency names.</li>
 *     <li>The numeric rates (units of target per 1 base).</li>
 * </ul>
 */
public class CompareCurrenciesOutputData {

    /** Name/code of the base currency that the user selected. */
    private final String baseCurrencyName;

    /** Names/codes of the target currencies being compared. */
    private final List<String> targetCurrencyNames;

    /** Exchange rates: each value is "target per 1 base". */
    private final List<Double> rates;

    /**
     * Creates a new output data object for the compare-currencies use case.
     *
     * @param baseCurrencyName    the base currency name/code
     * @param targetCurrencyNames list of target currency names/codes
     * @param rates               list of rates, aligned by index with targetCurrencyNames
     */
    public CompareCurrenciesOutputData(String baseCurrencyName,
                                       List<String> targetCurrencyNames,
                                       List<Double> rates) {
        this.baseCurrencyName = baseCurrencyName;
        this.targetCurrencyNames = targetCurrencyNames;
        this.rates = rates;
    }

    /**
     * @return the base currency name/code
     */
    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    /**
     * @return the list of target currency names/codes
     */
    public List<String> getTargetCurrencyNames() {
        return targetCurrencyNames;
    }

    /**
     * @return the list of exchange rates (target per 1 base)
     */
    public List<Double> getRates() {
        return rates;
    }
}
