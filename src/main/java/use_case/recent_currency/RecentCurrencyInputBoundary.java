package use_case.recent_currency;

/**
 * Input boundary for the Recent / Frequently Used Currencies use case (Use Case 8).
 *
 * The convert interactor (or a controller) calls this interface after a successful
 * conversion to record currency usage.
 */
public interface RecentCurrencyInputBoundary {

    /**
     * Executes the recent currency use case with the given input data.
     *
     * @param inputData information about which user and which currencies
     *                  were involved in the conversion.
     */
    void execute(RecentCurrencyInputData inputData);
}
