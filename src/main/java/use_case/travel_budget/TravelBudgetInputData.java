package use_case.travel_budget;

import java.util.List;

/**
 * Input data for the Travel Budget use case.
 * homeCurrencyName  – the currency in which we want the final total
 * itemCurrencyNames – currencies of individual expenses (same length as amounts)
 * amounts           – amounts for each expense
 */
public class TravelBudgetInputData {

    private final String homeCurrencyName;
    private final List<String> itemCurrencyNames;
    private final List<Double> amounts;

    /**
     * Constructs a new {@code TravelBudgetInputData}.
     *
     * @param homeCurrencyName  name of the currency used as the "home" or
     *                          base currency for the final total
     * @param itemCurrencyNames list of currency names for each expense item
     * @param amounts           list of amounts for each expense item,
     *                          in the matching currency from {@code itemCurrencyNames}
     */
    public TravelBudgetInputData(String homeCurrencyName,
                                 List<String> itemCurrencyNames,
                                 List<Double> amounts) {
        this.homeCurrencyName = homeCurrencyName;
        this.itemCurrencyNames = itemCurrencyNames;
        this.amounts = amounts;
    }

    /**
     * @return the name of the home currency
     */
    public String getHomeCurrencyName() {
        return homeCurrencyName;
    }


    /**
     * @return list of currency names for each expense item
     */
    public List<String> getItemCurrencyNames() {
        return itemCurrencyNames;
    }

    /**
     * @return list of amounts for each expense item
     */
    public List<Double> getAmounts() {
        return amounts;
    }
}
