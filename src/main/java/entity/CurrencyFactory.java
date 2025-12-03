package entity;

/**
 * Factory class responsible for creating valid {@link Currency} entities.
 * This class centralizes the logic for instantiating Currency objects, ensuring
 * consistency across the application. It specifically enforces the rule that
 * the currency's symbol property is initialized using its 3-letter ISO code.
 */
public class CurrencyFactory {

    /**
     * Creates a new {@link Currency} entity.
     *
     * @param name The full display name of the currency (e.g., "United States Dollar").
     * @param code The 3-letter ISO 4217 currency code (e.g., "USD").
     * @return A new, valid Currency object.
     */
    public Currency create(String name, String code) {
        // Enforce the logic that the 'symbol' field holds the 3-letter code
        return new Currency(name, code);
    }

}
