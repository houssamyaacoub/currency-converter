package use_case.compare_currencies;

import java.util.List;

/**
 * Simple data object that carries the user's choices
 * from the controller into the interactor.
 *
 * <p>It stores:
 * <ul>
 *     <li>Which currency is the base (the one on the left).</li>
 *     <li>Which currencies the user wants to compare against it.</li>
 * </ul>
 */
public class CompareCurrenciesInputData {

    /** Name/code of the base currency (e.g., "CAD"). */
    private final String baseCurrencyName;

    /** List of target currency names/codes that the user selected. */
    private final List<String> targetCurrencyNames;

    /**
     * Creates a new input data object for the compare-currencies use case.
     *
     * @param baseCurrencyName    the base currency name/code
     * @param targetCurrencyNames the list of target currencies to compare against the base
     */
    public CompareCurrenciesInputData(String baseCurrencyName, List<String> targetCurrencyNames) {
        this.baseCurrencyName = baseCurrencyName;
        this.targetCurrencyNames = targetCurrencyNames;
    }

    /**
     * Returns the base currency name/code.
     *
     * @return the base currency name/code
     */
    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    /**
     * Returns the list of target currency names/codes.
     *
     * @return the list of target currency names/codes
     */
    public List<String> getTargetCurrencyNames() {
        return targetCurrencyNames;
    }
}
