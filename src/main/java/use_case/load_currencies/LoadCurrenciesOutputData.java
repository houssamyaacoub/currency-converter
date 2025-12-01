package use_case.load_currencies;

import java.util.List;

/**
 * Output Data for the Load Currencies Use Case.
 */
public class LoadCurrenciesOutputData {
    private final List<String> currencyCodes;

    public LoadCurrenciesOutputData(List<String> currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public List<String> getCurrencyCodes() {
        return currencyCodes;
    }
}