package use_case.load_currencies;

import entity.Currency;
import org.junit.jupiter.api.Test;
import use_case.convert.CurrencyRepository;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadCurrenciesInteractorTest {

    @Test
    void successTest() {
        // Arrange
        CurrencyRepository repo = new StubCurrencyRepository();

        LoadCurrenciesOutputBoundary presenter = new LoadCurrenciesOutputBoundary() {
            @Override
            public void presentSuccessView(LoadCurrenciesOutputData outputData) {
                // Assert
                List<String> codes = outputData.getCurrencyNames();
                assertEquals(2, codes.size());
                assertTrue(codes.contains("Euro")); // Checks Names based on Stub logic
                assertTrue(codes.contains("US Dollar"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Success expected, got failure: " + errorMessage);
            }
        };

        LoadCurrenciesInteractor interactor = new LoadCurrenciesInteractor(repo, presenter);

        // Act
        interactor.execute();
    }

    @Test
    void failureEmptyListTest() {
        // Arrange
        CurrencyRepository repo = new CurrencyRepository() {
            @Override
            public Iterator<Currency> getCurrencyIterator() {
                return Collections.emptyIterator(); // Return empty iterator
            }
            // Unused methods
            @Override public Currency getByCode(String code) { return null; }
            @Override public List<Currency> getAllCurrencies() { return null; }
            @Override public Currency getByName(String name) { return null; }
        };

        LoadCurrenciesOutputBoundary presenter = new LoadCurrenciesOutputBoundary() {
            @Override
            public void presentSuccessView(LoadCurrenciesOutputData outputData) {
                fail("Failure expected due to empty list.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert
                assertEquals("Failed to load currency list: Currency list is empty. Check API key and file existence.", errorMessage);
            }
        };

        LoadCurrenciesInteractor interactor = new LoadCurrenciesInteractor(repo, presenter);

        // Act
        interactor.execute();
    }

    @Test
    void failureRepositoryExceptionTest() {
        // Arrange
        CurrencyRepository repo = new CurrencyRepository() {
            @Override
            public Iterator<Currency> getCurrencyIterator() {
                throw new RuntimeException("Database connection failed");
            }
            @Override public Currency getByCode(String code) { return null; }
            @Override public List<Currency> getAllCurrencies() { return null; }
            @Override public Currency getByName(String name) { return null; }
        };

        LoadCurrenciesOutputBoundary presenter = new LoadCurrenciesOutputBoundary() {
            @Override
            public void presentSuccessView(LoadCurrenciesOutputData outputData) {
                fail("Failure expected due to repository exception.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert
                assertEquals("Failed to load currency list: Database connection failed", errorMessage);
            }
        };

        LoadCurrenciesInteractor interactor = new LoadCurrenciesInteractor(repo, presenter);

        // Act
        interactor.execute();
    }

    // --- Stub ---
    private static class StubCurrencyRepository implements CurrencyRepository {
        @Override
        public Iterator<Currency> getCurrencyIterator() {
            // Return a simple iterator with 2 currencies
            List<Currency> list = List.of(
                    new Currency("US Dollar", "USD"),
                    new Currency("Euro", "EUR")
            );
            return list.iterator();
        }

        @Override public Currency getByCode(String code) { return null; }
        @Override public List<Currency> getAllCurrencies() { return null; }
        @Override public Currency getByName(String name) { return null; }
    }
}