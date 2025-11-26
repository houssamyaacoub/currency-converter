package use_case.favourite_currency;

/**
 * Input data for the Favourite Currency use case (Use Case 5).
 *
 * This object is created by the controller and passed to the interactor.
 */
public class FavouriteCurrencyInputData {

    private final String userId;
    private final String currencyCode;
    private final boolean markAsFavourite;

    /**
     * Constructs a new FavouriteCurrencyInputData.
     *
     * @param userId          the unique identifier of the user.
     * @param currencyCode    the currency code (e.g., "CAD", "USD").
     * @param markAsFavourite true if the currency should be added to favourites,
     *                        false if it should be removed.
     */
    public FavouriteCurrencyInputData(String userId,
                                      String currencyCode,
                                      boolean markAsFavourite) {
        this.userId = userId;
        this.currencyCode = currencyCode;
        this.markAsFavourite = markAsFavourite;
    }

    public String getUserId() {
        return userId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public boolean isMarkAsFavourite() {
        return markAsFavourite;
    }
}
