package use_case.favourite_currency;

/**
 * Input data for the Favourite Currency use case (Use Case 5).
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

    /**
     * Returns the identifier of the user whose favourites are being modified.
     *
     * @return the user id
     */

    public String getUserId() {
        return userId;
    }

    /**
     * Returns the currency code to be toggled as a favourite.
     *
     * @return the currency code (for example, {@code "CAD"})
     */

    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Indicates whether the currency should be marked as favourite ({@code true})
     * or removed from favourites ({@code false}).
     *
     * @return {@code true} to add to favourites, {@code false} to remove
     */

    public boolean isMarkAsFavourite() {
        return markAsFavourite;
    }
}
