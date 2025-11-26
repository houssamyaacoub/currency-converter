package use_case.recent_currency;

/**
 * Input data for the Recent / Frequently Used Currencies use case (Use Case 8).
 *
 * This object is typically created by the convert interactor or controller
 * after a successful conversion.
 */
public class RecentCurrencyInputData {

    private final String userId;
    private final String fromCurrencyCode;
    private final String toCurrencyCode;

    /**
     * Constructs a new RecentCurrencyInputData.
     *
     * @param userId           the unique identifier of the user.
     * @param fromCurrencyCode the "from" currency code (e.g., "CAD").
     * @param toCurrencyCode   the "to" currency code (e.g., "USD").
     */
    public RecentCurrencyInputData(String userId,
                                   String fromCurrencyCode,
                                   String toCurrencyCode) {
        this.userId = userId;
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public String getToCurrencyCode() {
        return toCurrencyCode;
    }
}
