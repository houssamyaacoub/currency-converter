package entity;

/**
 * Factory class responsible for creating valid Currency entities.
 * Centralizes the logic for determining the 'symbol' (ISO Code).
 */
public class CurrencyFactory {

    /**
     * Creates a new Currency entity.
     * @param name The full currency name (e.g., "United States Dollar").
     * @param code The 3-letter ISO code (e.g., "USD").
     * @return A new Currency object.
     */
    public Currency create(String name, String code) {
        // Enforce the logic that the 'symbol' field holds the 3-letter code
        return new Currency(name, code);
    }
}