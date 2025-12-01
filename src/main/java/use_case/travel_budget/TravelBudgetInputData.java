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

    public TravelBudgetInputData(String homeCurrencyName,
                                 List<String> itemCurrencyNames,
                                 List<Double> amounts) {
        this.homeCurrencyName = homeCurrencyName;
        this.itemCurrencyNames = itemCurrencyNames;
        this.amounts = amounts;
    }

    public String getHomeCurrencyName() {
        return homeCurrencyName;
    }

    public List<String> getItemCurrencyNames() {
        return itemCurrencyNames;
    }

    public List<Double> getAmounts() {
        return amounts;
    }
}
