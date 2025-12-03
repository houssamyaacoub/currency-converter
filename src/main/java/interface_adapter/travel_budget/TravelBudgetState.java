package interface_adapter.travel_budget;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for the Travel Budget screen.
 *
 * <p>This class holds all UI-relevant values such as:
 * <ul>
 *   <li>The selected home currency</li>
 *   <li>The formatted total</li>
 *   <li>The formatted breakdown list</li>
 *   <li>Any error message</li>
 * </ul></p>
 *
 * <p>It is updated by the presenter and observed by the view.</p>
 */
public class TravelBudgetState {

    private String homeCurrency;
    private String totalFormatted;          // e.g. "1234.56 CAD"
    private List<String> lineItems = new ArrayList<>();
    private String error;

    public TravelBudgetState() {}

    public TravelBudgetState(TravelBudgetState copy) {
        this.homeCurrency = copy.homeCurrency;
        this.totalFormatted = copy.totalFormatted;
        this.lineItems = new ArrayList<>(copy.lineItems);
        this.error = copy.error;
    }

    public String getHomeCurrency() {
        return homeCurrency;
    }

    public void setHomeCurrency(String homeCurrency) {
        this.homeCurrency = homeCurrency;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    public List<String> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<String> lineItems) {
        this.lineItems = lineItems;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
