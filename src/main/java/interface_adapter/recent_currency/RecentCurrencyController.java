package interface_adapter.recent_currency;

import use_case.recent_currency.RecentCurrencyInputBoundary;
import use_case.recent_currency.RecentCurrencyInputData;

/**
 * Controller for the Recent / Frequently Used Currencies feature (Use Case 8).
 * This controller is typically called after a successful conversion
 * to notify the RecentCurrencyInteractor that a given pair of currencies
 * has just been used.
 */
public class RecentCurrencyController {

    private final RecentCurrencyInputBoundary interactor;

    /**
     * Constructs a new RecentCurrencyController.
     *
     * @param interactor the input boundary for the recent currency use case.
     */
    public RecentCurrencyController(RecentCurrencyInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Records usage of the given currency pair for the specified user.
     *
     * @param userId           the current user's ID.
     * @param fromCurrencyCode the "from" currency code.
     * @param toCurrencyCode   the "to" currency code.
     */
    public void execute(String userId, String fromCurrencyCode, String toCurrencyCode) {
        RecentCurrencyInputData inputData =
                new RecentCurrencyInputData(userId, fromCurrencyCode, toCurrencyCode);
        interactor.execute(inputData);
    }
}
