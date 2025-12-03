package interface_adapter.travel_budget;

import use_case.travel_budget.TravelBudgetInputBoundary;
import use_case.travel_budget.TravelBudgetInputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Travel Budget feature.
 * The View will pass:
 *  - homeCurrencyName (String)
 *  - itemCurrencyNames (List<String>)
 *  - amountStrings (List<String>) from text fields
 */
public class TravelBudgetController {

    private final TravelBudgetInputBoundary interactor;

    /**
     * Creates a new TravelBudgetController.
     *
     * @param interactor the input boundary that will process travel budget requests
     */
    public TravelBudgetController(TravelBudgetInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Packages user data from the view into {@link TravelBudgetInputData}
     * and sends it to the interactor.
     *
     * @param homeCurrencyName the selected home currency (e.g., "Canadian Dollar")
     * @param itemCurrencyNames list of currencies for each expense item
     * @param amountStrings list of numeric strings entered by the user
     */
    public void execute(String homeCurrencyName,
                        List<String> itemCurrencyNames,
                        List<String> amountStrings) {

        List<Double> amounts = new ArrayList<>();

        for (String s : amountStrings) {
            try {
                amounts.add(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                // Treat invalid inputs as 0; Interactor can still validate further if needed
                amounts.add(0.0);
            }
        }

        TravelBudgetInputData inputData =
                new TravelBudgetInputData(homeCurrencyName, itemCurrencyNames, amounts);

        interactor.execute(inputData);
    }

    /**
     * Tells the interactor to navigate back to the Home view.
     */
    public void switchToHome() {
        interactor.switchToHomeView();
    }
}
