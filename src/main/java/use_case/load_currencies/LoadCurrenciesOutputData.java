package use_case.load_currencies;

import java.util.List;

/**
 * Output Data for the Load Currencies Use Case.
 * This Data Transfer Object (DTO) carries the list of available currency codes/names
 * from the {@link LoadCurrenciesInteractor} to the {@link LoadCurrenciesOutputBoundary} (Presenter).
 * It encapsulates the data required to populate UI selection components.
 */
public class LoadCurrenciesOutputData {
    private final List<String> currencyCodes;

    /**
     * Constructs a new LoadCurrenciesOutputData instance.
     *
     * @param currencyCodes A list of strings representing the available currencies
     */
    public LoadCurrenciesOutputData(List<String> currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    /**
     * Retrieves the list of currency names.
     *
     * @return A List of currency strings.
     */
    public List<String> getCurrencyNames() {
        return currencyCodes;
    }
}