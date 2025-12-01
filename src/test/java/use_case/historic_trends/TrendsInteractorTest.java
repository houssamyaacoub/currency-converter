package use_case.historic_trends;

import entity.Currency;
import entity.CurrencyConversion;
import org.junit.jupiter.api.Test;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrendsInteractorTest {

    @Test
    void successTest() {
        // 1. Arrange
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", "1 week");

        // We use our "In-Memory" stubs defined at the bottom of this file
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        // 2. Define the Presenter (Anonymous Class style)
        TrendsOutputBoundary successPresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                // Assert: Check that the data is correct
                assertEquals("Euro", output.getBaseCurrency());
                assertEquals("US Dollar", output.getTargetCurrency());

                // Check if we got the data from the DAO
                assertFalse(output.getRates().isEmpty());
                assertEquals(1.5, output.getRates().get(0));

                // Verify the DAO received the correct date calculation (1 week ago)
                LocalDate expectedStart = LocalDate.now().minusWeeks(1);
                assertEquals(expectedStart, dao.lastStart);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void prepareHomeView() {
                // Not expected in this test
            }
        };

        // 3. Act
        TrendsInteractor interactor = new TrendsInteractor(dao, repo, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void successOneYearTest() {
        // Testing the Switch Statement logic for "1 year"
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", "1 year");
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary successPresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                // Verify date math for 1 year
                LocalDate expectedStart = LocalDate.now().minusYears(1);
                assertEquals(expectedStart, dao.lastStart);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void successSixMonthsTest() {
        // Testing the Switch Statement logic for "6 months"
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", "6 months");
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary successPresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                LocalDate expectedStart = LocalDate.now().minusMonths(6);
                assertEquals(expectedStart, dao.lastStart);
            }

            @Override
            public void prepareFailView(String error) { fail("Unexpected fail"); }
            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void successOneMonthTest() {
        // Testing the Switch Statement logic for "1 month"
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", "1 month");
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary successPresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                LocalDate expectedStart = LocalDate.now().minusMonths(1);
                assertEquals(expectedStart, dao.lastStart);
            }

            @Override
            public void prepareFailView(String error) { fail("Unexpected fail"); }
            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureNullInputTest() {
        // Test 1: Null Time Period
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", null);
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary failurePresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                fail("Success unexpected when time period is null");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Error: Time period cannot be empty.", error);
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureNullCurrencyTest() {
        // Test 2: Null Currency
        TrendsInputData inputData = new TrendsInputData(null, "US Dollar", "1 week");
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary failurePresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                fail("Success unexpected when currency is null");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Error: Currency selection cannot be empty.", error);
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureGhostBaseCurrencyTest() {
        // Test 3: Currency not found in Repository
        TrendsInputData inputData = new TrendsInputData("Ghost Coin", "US Dollar", "1 week");

        // This repo returns NULL for "Ghost Coin"
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary failurePresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                fail("Success unexpected when currency does not exist");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("not supported"));
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureGhostTargetCurrencyTest() {
        // Test 10: Valid Base, but Ghost Target
        TrendsInputData inputData = new TrendsInputData("US Dollar", "Ghost Coin", "1 week");

        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        TrendsOutputBoundary failurePresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                fail("Success unexpected when target currency does not exist");
            }

            @Override
            public void prepareFailView(String error) {
                // Verify we caught the TARGET error specifically
                assertTrue(error.contains("Target currency 'Ghost Coin' not supported"));
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureSystemCrashTest() {
        // Test 4: DAO throws an exception
        TrendsInputData inputData = new TrendsInputData("Euro", "US Dollar", "1 week");
        StubCurrencyRepository repo = new StubCurrencyRepository();

        // Configure DAO to crash
        StubExchangeRateDAO dao = new StubExchangeRateDAO();
        dao.shouldFail = true;

        TrendsOutputBoundary failurePresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {
                fail("Success unexpected when DAO crashes");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("System Error"));
                assertTrue(error.contains("Simulated API Error"));
            }

            @Override
            public void prepareHomeView() {}
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void navigationTest() {
        // Test the switchToHomeView method
        StubCurrencyRepository repo = new StubCurrencyRepository();
        StubExchangeRateDAO dao = new StubExchangeRateDAO();

        // Create a simple flag wrapper to check if method was called
        final boolean[] wasCalled = {false};

        TrendsOutputBoundary navPresenter = new TrendsOutputBoundary() {
            @Override
            public void prepareSuccessView(TrendsOutputData output) {}
            @Override
            public void prepareFailView(String error) {}

            @Override
            public void prepareHomeView() {
                wasCalled[0] = true;
            }
        };

        TrendsInteractor interactor = new TrendsInteractor(dao, repo, navPresenter);
        interactor.switchToHomeView();

        assertTrue(wasCalled[0]);
    }

    // ------------------------------------------------------------------
    // Inner classes

    /**
     * Stub Repository that returns a valid Currency unless name is "Ghost Coin".
     */
    private static class StubCurrencyRepository implements CurrencyRepository {
        @Override
        public Currency getByName(String name) {
            if ("Ghost Coin".equals(name)) {
                return null;
            }
            return new Currency(name, "CODE");
        }

        @Override
        public Currency getByCode(String code) { return null; } // Unused
        @Override
        public List<Currency> getAllCurrencies() { return Collections.emptyList(); } // Unused
    }

    /**
     * Stub DAO that captures input dates and returns dummy data or throws exception.
     */
    private static class StubExchangeRateDAO implements ExchangeRateDataAccessInterface {
        boolean shouldFail = false;
        LocalDate lastStart;
        LocalDate lastEnd;

        @Override
        public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to, LocalDate start, LocalDate end) {
            this.lastStart = start;
            this.lastEnd = end;

            if (shouldFail) {
                throw new RuntimeException("Simulated API Error");
            }

            // Return 1 dummy conversion so success view logic runs
            List<CurrencyConversion> list = new ArrayList<>();
            Instant instant = start.atStartOfDay(ZoneId.of("UTC")).toInstant();
            list.add(new CurrencyConversion(from, to, 1.5, instant));
            return list;
        }

        @Override
        public CurrencyConversion getLatestRate(Currency from, Currency to) { return null; } // Unused
    }
}