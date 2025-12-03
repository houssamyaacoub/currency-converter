package entity;

/**
 * Entity representing a Currency.
 * This class encapsulates the core properties of a currency, such as its
 * full display name (e.g., "United States Dollar") and its unique symbol or ISO code
 * (e.g., "USD").
 * As an Entity, this class resides in the innermost layer of the Clean Architecture
 * and should not depend on any outer layers (such as Data Access or Views).
 */
public class Currency {

    private final String name;
    private final String symbol;

    /**
     * Constructs a new Currency entity.
     *
     * @param name   The full display name of the currency.
     * @param symbol The symbol or ISO code representing the currency.
     */
    public Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Retrieves the full name of the currency.
     *
     * @return The currency name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the symbol or ISO code of the currency.
     *
     * @return The currency symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
