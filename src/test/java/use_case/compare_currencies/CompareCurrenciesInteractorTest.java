package use_case.compare_currencies;

import entity.Currency;
import entity.CurrencyConversion;
import org.junit.jupiter.api.Test;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Use Case 6: CompareCurrenciesInteractor.
 *
 * We only test the interactor here. We stub:
 *  - CurrencyRepository (so we control which Currency objects exist)
 *  - ExchangeRateDataAccessInterface (so we control the rates it returns)
 *  - CompareCurrenciesOutputBoundary (so we can assert on the output)
 */
class CompareCurrenciesInteractorTest {

    /**
     * Simple stub of CurrencyRepository that stores currencies in a Map by name.
     */
    private static class StubCurrencyRepository implements CurrencyRepository {

        private final Map<String, Currency> byName = new HashMap<>();

        void addCurrency(String name, String code) {
            byName.put(name, new Currency(name, code));
        }

        @Override
        public Currency getByCode(String code) {
            // not used in these tests
            throw new UnsupportedOperationException("getByCode not needed in this test");
        }

        @Override
        public Iterator<Currency> getCurrencyIterator() {
            return byName.values().iterator();
        }

        @Override
        public List<Currency> getAllCurrencies() {
            return new ArrayList<>(byName.values());
        }

        @Override
        public Currency getByName(String name) {
            Currency c = byName.get(name);
            if (c == null) {
                throw new IllegalArgumentException("Unknown currency: " + name);
            }
            return c;
        }
    }

    /**
     * Stub DAO that returns fixed rates for each target currency name.
     */
    private static class StubExchangeRateDAO implements ExchangeRateDataAccessInterface {

        private final Map<String, Double> rateByTargetName;

        StubExchangeRateDAO(Map<String, Double> rateByTargetName) {
            this.rateByTargetName = rateByTargetName;
        }

        @Override
        public CurrencyConversion getLatestRate(Currency from, Currency to) {
            double rate = rateByTargetName.getOrDefault(to.getName(), 1.0);
            return new CurrencyConversion(from, to, rate, Instant.now());
        }

        @Override
        public List<CurrencyConversion> getHistoricalRates(Currency from, Currency to,
                                                           LocalDate start, LocalDate end) {
            // not used in this use case
            return Collections.emptyList();
        }
    }

    @Test
    void successTest_multipleTargets() {
        // --- Arrange ---
        StubCurrencyRepository repo = new StubCurrencyRepository();
        repo.addCurrency("Turkish Lira", "TRY");
        repo.addCurrency("US Dollar", "USD");
        repo.addCurrency("Euro", "EUR");
        repo.addCurrency("Japanese Yen", "JPY");

        // We control the rates that the DAO returns for each target name
        Map<String, Double> rateMap = new HashMap<>();
        rateMap.put("US Dollar", 0.032); // example: 1 TRY = 0.032 USD
        rateMap.put("Euro", 0.029);
        rateMap.put("Japanese Yen", 4.50);

        StubExchangeRateDAO dao = new StubExchangeRateDAO(rateMap);

        // Input: base + list of targets
        List<String> targets = Arrays.asList("US Dollar", "Euro", "Japanese Yen");
        CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData("Turkish Lira", targets);

        CompareCurrenciesOutputBoundary presenter = new CompareCurrenciesOutputBoundary() {
            @Override
            public void present(CompareCurrenciesOutputData data) {
                // We expect the base to be passed through unchanged
                assertEquals("Turkish Lira", data.getBaseCurrencyName());

                // Targets should come back in the same order (minus any filtered ones)
                assertEquals(targets, data.getTargetCurrencyNames());

                // Check rates are what our stub DAO provided
                assertEquals(3, data.getRates().size());
                assertEquals(0.032, data.getRates().get(0));
                assertEquals(0.029, data.getRates().get(1));
                assertEquals(4.50, data.getRates().get(2));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected in successTest. Error: " + errorMessage);
            }
        };

        CompareCurrenciesInteractor interactor =
                new CompareCurrenciesInteractor(dao, repo, presenter);

        // --- Act ---
        interactor.execute(inputData);
    }

    @Test
    void failureTest_noTargetsSelected() {
        StubCurrencyRepository repo = new StubCurrencyRepository();
        repo.addCurrency("Turkish Lira", "TRY");

        StubExchangeRateDAO dao = new StubExchangeRateDAO(Collections.emptyMap());

        CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData("Turkish Lira", Collections.emptyList());

        CompareCurrenciesOutputBoundary presenter = new CompareCurrenciesOutputBoundary() {
            @Override
            public void present(CompareCurrenciesOutputData data) {
                fail("present should not be called when there are no targets.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Please select at least one target currency.", errorMessage);
            }
        };

        CompareCurrenciesInteractor interactor =
                new CompareCurrenciesInteractor(dao, repo, presenter);

        interactor.execute(inputData);
    }

    @Test
    void failureTest_tooManyTargets() {
        StubCurrencyRepository repo = new StubCurrencyRepository();
        repo.addCurrency("Turkish Lira", "TRY");
        // just some dummy currencies – the interactor will fail before looking them up
        repo.addCurrency("A", "A");
        repo.addCurrency("B", "B");
        repo.addCurrency("C", "C");
        repo.addCurrency("D", "D");
        repo.addCurrency("E", "E");
        repo.addCurrency("F", "F");

        StubExchangeRateDAO dao = new StubExchangeRateDAO(Collections.emptyMap());

        List<String> sixTargets = Arrays.asList("A", "B", "C", "D", "E", "F");
        CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData("Turkish Lira", sixTargets);

        CompareCurrenciesOutputBoundary presenter = new CompareCurrenciesOutputBoundary() {
            @Override
            public void present(CompareCurrenciesOutputData data) {
                fail("present should not be called when there are too many targets.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("You can compare at most 5 currencies.", errorMessage);
            }
        };

        CompareCurrenciesInteractor interactor =
                new CompareCurrenciesInteractor(dao, repo, presenter);

        interactor.execute(inputData);
    }

    @Test
    void failureTest_allTargetsSameAsBase() {
        StubCurrencyRepository repo = new StubCurrencyRepository();
        repo.addCurrency("Turkish Lira", "TRY");

        // Even if DAO had data, interactor should filter everything out
        StubExchangeRateDAO dao = new StubExchangeRateDAO(Collections.emptyMap());

        List<String> targets = Arrays.asList("Turkish Lira", "Turkish Lira");
        CompareCurrenciesInputData inputData =
                new CompareCurrenciesInputData("Turkish Lira", targets);

        CompareCurrenciesOutputBoundary presenter = new CompareCurrenciesOutputBoundary() {
            @Override
            public void present(CompareCurrenciesOutputData data) {
                fail("present should not be called when no valid targets remain.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("No valid target currencies selected.", errorMessage);
            }
        };

        CompareCurrenciesInteractor interactor =
                new CompareCurrenciesInteractor(dao, repo, presenter);

        interactor.execute(inputData);
    }
}
