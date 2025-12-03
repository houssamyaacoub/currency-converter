package use_case.travel_budget;

import java.util.List;

/**
 * Output data object for the Travel Budget use case.
 * <br>
 * This is created by the interactor and passed to the presenter.
 */
public class TravelBudgetOutputData {

    private final String homeCurrency;          // e.g. Canadian dollar
    private final double totalInHomeCurrency;   // numeric total
    private final List<String> lineItems;       // one string per item, already formatted

    /**
     * Constructs a new {@code TravelBudgetOutputData}.
     *
     * @param homeCurrency        name of the home currency
     * @param totalInHomeCurrency total amount converted into the home currency
     * @param lineItems           formatted breakdown lines for each expense item
     * @param b       whether the use case failed (true) or succeeded (false)
     */
    public TravelBudgetOutputData(String homeCurrency,
                                  double totalInHomeCurrency,
                                  List<String> lineItems, boolean b) {
        this.homeCurrency = homeCurrency;
        this.totalInHomeCurrency = totalInHomeCurrency;
        this.lineItems = lineItems;
    }

    /**
     * @return the name of the home currency
     */
    public String getHomeCurrency() {
        return homeCurrency;
    }

    /**
     * @return formatted breakdown lines for each expense item
     */
    public double getTotalInHomeCurrency() {
        return totalInHomeCurrency;
    }

    /**
     * @return {@code true} if the use case failed, {@code false} otherwise
     */
    public List<String> getLineItems() {
        return lineItems;
    }
}
