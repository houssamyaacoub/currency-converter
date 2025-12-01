package use_case.travel_budget;

import java.util.List;

public class TravelBudgetOutputData {

    private final String homeCurrency;          // e.g. Canadian dollar
    private final double totalInHomeCurrency;   // numeric total
    private final List<String> lineItems;       // one string per item, already formatted

    public TravelBudgetOutputData(String homeCurrency,
                                  double totalInHomeCurrency,
                                  List<String> lineItems, boolean b) {
        this.homeCurrency = homeCurrency;
        this.totalInHomeCurrency = totalInHomeCurrency;
        this.lineItems = lineItems;
    }

    public String getHomeCurrency() {
        return homeCurrency;
    }

    public double getTotalInHomeCurrency() {
        return totalInHomeCurrency;
    }

    public List<String> getLineItems() {
        return lineItems;
    }
}
