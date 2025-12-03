package use_case.convert;

import entity.Currency;
import entity.CurrencyConversion;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvertCurrencyInteractorTest {

    @Test
    void successTest() {
        // Arrange
        ConvertInputData inputData = new ConvertInputData(100.0, "USD", "EUR");

        // Stub Repository that successfully finds currencies
        CurrencyRepository repo = new StubCurrencyRepository();

        // Stub DAO that returns a rate of 0.85
        ExchangeRateDataAccessInterface dao = new StubExchangeRateDAO(0.85);

        // Mock Presenter to verify output
        ConvertOutputBoundary presenter = new ConvertOutputBoundary() {
            @Override
            public void present(ConvertOutputData outputData) {
                // Assertions for SUCCESS
                assertEquals(85.0, outputData.getConvertedAmount(), 0.001);
                assertEquals(0.85, outputData.getRate(), 0.001);
                assertEquals("EUR", outputData.getTargetCurrencyCode());
                assertNotNull(outputData.getTimestamp());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected.");
            }
        };

        ConvertCurrencyInteractor interactor = new ConvertCurrencyInteractor(dao, presenter, repo);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureCurrencyNotFoundTest() {
        // Arrange
        ConvertInputData inputData = new ConvertInputData(100.0, "MarsCoin", "EUR");

        // Repository that throws exception for unknown currency
        CurrencyRepository repo = new CurrencyRepository() {
            @Override
            public Currency getByName(String name) {
                throw new RuntimeException("Currency not found: " + name);
            }
            // ... other unused methods stubbed ...
            @Override public Currency getByCode(String code) { return null; }
            @Override public List<Currency> getAllCurrencies() { return null; }
            @Override public Iterator<Currency> getCurrencyIterator() { return null; }
        };

        ExchangeRateDataAccessInterface dao = new StubExchangeRateDAO(1.0); // Unused

        ConvertOutputBoundary presenter = new ConvertOutputBoundary() {
            @Override
            public void present(ConvertOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assertions for FAILURE
                assertEquals("Currency not found: MarsCoin", errorMessage);
            }
        };

        ConvertCurrencyInteractor interactor = new ConvertCurrencyInteractor(dao, presenter, repo);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureDataAccessErrorTest() {
        // Arrange
        ConvertInputData inputData = new ConvertInputData(100.0, "USD", "EUR");
        CurrencyRepository repo = new StubCurrencyRepository();

        // DAO that simulates API failure
        ExchangeRateDataAccessInterface dao = new ExchangeRateDataAccessInterface() {
            @Override
            public CurrencyConversion getLatestRate(Currency from, Currency to) {
                throw new RuntimeException("API Offline");
            }

            @Override
            public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end) {
                return null;
            }
        };

        ConvertOutputBoundary presenter = new ConvertOutputBoundary() {
            @Override
            public void present(ConvertOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("API Offline", errorMessage);
            }
        };

        ConvertCurrencyInteractor interactor = new ConvertCurrencyInteractor(dao, presenter, repo);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureNullMessageTest() {
        // Arrange
        ConvertInputData inputData = new ConvertInputData(100.0, "USD", "EUR");
        CurrencyRepository repo = new StubCurrencyRepository();

        ExchangeRateDataAccessInterface dao = new ExchangeRateDataAccessInterface() {
            @Override
            public CurrencyConversion getLatestRate(Currency from, Currency to) {
                // CHANGE THIS: Use the constructor with NO arguments to get a NULL message
                throw new RuntimeException();
            }
            @Override
            public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end) { return null; }
        };

        ConvertOutputBoundary presenter = new ConvertOutputBoundary() {
            @Override
            public void present(ConvertOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // This asserts that the NULL check (Path A) worked correctly
                assertEquals("Conversion failed due to an unknown system error.", errorMessage);
            }
        };

        ConvertCurrencyInteractor interactor = new ConvertCurrencyInteractor(dao, presenter, repo);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureEmptyMessageTest() {
        // Arrange
        ConvertInputData inputData = new ConvertInputData(100.0, "USD", "EUR");
        CurrencyRepository repo = new StubCurrencyRepository();

        ExchangeRateDataAccessInterface dao = new ExchangeRateDataAccessInterface() {
            @Override
            public CurrencyConversion getLatestRate(Currency from, Currency to) {
                // THIS IS THE KEY: A non-null, but EMPTY string
                throw new RuntimeException("");
            }
            @Override
            public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end) { return null; }
        };

        ConvertOutputBoundary presenter = new ConvertOutputBoundary() {
            @Override
            public void present(ConvertOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert that the empty string triggered the default message logic
                assertEquals("Conversion failed due to an unknown system error.", errorMessage);
            }
        };

        ConvertCurrencyInteractor interactor = new ConvertCurrencyInteractor(dao, presenter, repo);

        // Act
        interactor.execute(inputData);
    }

    // --- Test Helpers (Stubs) ---

    private static class StubCurrencyRepository implements CurrencyRepository {
        @Override
        public Currency getByName(String name) {
            // Simple logic: assumes name is valid and creates dummy entity
            return new Currency(name, name); // name used as code for simplicity
        }

        // Unused methods required by interface
        @Override public Currency getByCode(String code) { return null; }
        @Override public List<Currency> getAllCurrencies() { return Collections.emptyList(); }
        @Override public Iterator<Currency> getCurrencyIterator() { return Collections.emptyIterator(); }
    }

    private static class StubExchangeRateDAO implements ExchangeRateDataAccessInterface {
        private final double fixedRate;

        public StubExchangeRateDAO(double fixedRate) {
            this.fixedRate = fixedRate;
        }

        @Override
        public CurrencyConversion getLatestRate(Currency from, Currency to) {
            return new CurrencyConversion(from, to, fixedRate, Instant.now());
        }

        @Override
        public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end) {
            return Collections.emptyList();
        }
    }
}