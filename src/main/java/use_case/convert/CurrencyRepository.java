package use_case.convert;

import entity.Currency;

import java.util.List;
import java.util.Iterator;

/**
 * Repository Gateway (Port) for accessing Currency Entities.
 * This interface defines the contract that the Interactor requires to look up
 * Currency Entities. It lives in the Use Case layer (Application Business Rules)
 * and allows the core logic to access data without depending on the outer layers.
 */
public interface CurrencyRepository {

    /**
     * Retrieves a Currency entity by its unique ISO code.
     *
     * @param code The 3-letter ISO currency code (e.g., "USD").
     * @return The Currency entity.
     * @throws IllegalArgumentException if the code is not valid or supported.
     */
    Currency getByCode(String code);

    /**
     * Retrieves the complete list of supported Currency Entities.
     * This method provides a snapshot of all available currencies, useful for
     * scenarios where the full collection size or random access is needed.
     *
     * @return A List of all Currency objects.
     */
    List<Currency> getAllCurrencies();

    /**
     * Retrieves a Currency entity by its full display name.
     *
     * @param name The full name of the currency (e.g., "United States Dollar").
     * @return The Currency entity.
     * @throws IllegalArgumentException if the name is not found.
     */
    Currency getByName(String name);

    /**
     * Returns an iterator over the collection of all supported Currency Entities.
     * This implements the Iterator Pattern, allowing Use Cases to traverse
     * the available currencies without being coupled to the specific underlying
     * collection structure (such as List or Map).
     *
     * @return An Iterator for Currency objects.
     */
    Iterator<Currency> getCurrencyIterator();
}