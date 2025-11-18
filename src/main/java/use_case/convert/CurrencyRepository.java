package use_case.convert;

import entity.Currency;

/**
 * Repository Gateway (Port) for accessing Currency Entities.
 * * This interface defines the contract that the Interactor requires to look up
 * a Currency Entity based on its code. It lives in the Use Case layer.
 */
public interface CurrencyRepository {

    Currency getByCode(String code); // Renamed method for clarity
}