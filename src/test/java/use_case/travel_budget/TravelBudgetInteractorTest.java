package use_case.travel_budget;

import entity.Currency;
import entity.CurrencyConversion;
import use_case.convert.CurrencyRepository;
import use_case.convert.ExchangeRateDataAccessInterface;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TravelBudgetInteractor}.
 */
class TravelBudgetInteractorTest {

    /* ---------- Test doubles ---------- */

    /**
     * Simple in-memory CurrencyRepository test double.
     */
    private static class FakeCurrencyRepository implements CurrencyRepository {

        private final Map<String, Currency> currenciesByName = new HashMap<>();

        FakeCurrencyRepository(List<Currency> currencies) {
            for (Currency c : currencies) {
                currenciesByName.put(c.getName(), c);
            }
        }

        @Override
        public Currency getByCode(String code) {
            // Not needed for these tests
            return null;
        }

        @Override
        public List<Currency> getAllCurrencies() {
            return new ArrayList<>(currenciesByName.values());
        }

        @Override
        public Currency getByName(String name) {
            return currenciesByName.get(name);
        }

        @Override
        public Iterator<Currency> getCurrencyIterator() {
            return currenciesByName.values().iterator();
        }
    }

    /**
     * Test double for the data access; always returns a fixed conversion.
     */
    private static class FakeExchangeRates implements ExchangeRateDataAccessInterface {

        private final double rate;

        FakeExchangeRates(double rate) {
            this.rate = rate;
        }

        @Override
        public CurrencyConversion getLatestRate(Currency from, Currency to) {
            // Assumes CurrencyConversion has a constructor (from, to, rate, timestamp).
            return new CurrencyConversion(from, to, rate, Instant.now());
        }

        @Override
        public List<CurrencyConversion> getHistoricalRates(
                Currency from, Currency to, LocalDate start, LocalDate end) {
            throw new UnsupportedOperationException("Not needed in these tests");
        }
    }

    /**
     * Data-access double that always throws, to hit the exception path.
     */
    private static class ThrowingExchangeRates implements ExchangeRateDataAccessInterface {

        @Override
        public CurrencyConversion getLatestRate(Currency from, Currency to) {
            throw new RuntimeException("boom");
        }

        @Override
        public List<CurrencyConversion> getHistoricalRates(
                Currency from, Currency to, LocalDate start, LocalDate end) {
            throw new UnsupportedOperationException("Not needed in these tests");
        }
    }

    /**
     * Presenter that just records the last success or error.
     */
    private static class RecordingPresenter implements TravelBudgetOutputBoundary {

        private TravelBudgetOutputData lastSuccess;
        private String lastError;
        private boolean homeViewCalled;

        @Override
        public void prepareSuccessView(TravelBudgetOutputData data) {
            this.lastSuccess = data;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.lastError = errorMessage;
        }

        @Override
        public void prepareHomeView() {
            this.homeViewCalled = true;
        }
    }

    /** Helper to build an interactor with common fakes. */
    private TravelBudgetInteractor makeInteractor(
            ExchangeRateDataAccessInterface rates,
            CurrencyRepository repo,
            RecordingPresenter presenter) {
        return new TravelBudgetInteractor(rates, repo, presenter);
    }

    /* ---------- Tests hitting all paths ---------- */

    @Test
    void execute_successfulConversion_buildsTotalAndBreakdown() {
        Currency cad = new Currency("CAD", "$");
        Currency eur = new Currency("EUR", "€");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad, eur));
        FakeExchangeRates rates = new FakeExchangeRates(2.0); // 1 EUR = 2 CAD

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        List<String> itemCurrencies = List.of("EUR");
        List<Double> amounts = List.of(10.0);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", itemCurrencies, amounts);

        interactor.execute(input);

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        TravelBudgetOutputData out = presenter.lastSuccess;
        assertEquals("CAD", out.getHomeCurrency());
        assertEquals(20.0, out.getTotalInHomeCurrency(), 1e-6);
        assertEquals(1, out.getLineItems().size());
        assertTrue(out.getLineItems().get(0).contains("10.00 EUR"));
    }

    @Test
    void execute_failsWhenListsAreNull() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        // names is null, amounts non-null
        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", null, List.of(10.0));

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void execute_failsWhenListsEmpty() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", List.of(), List.of());

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void execute_failsWhenListSizesMismatch() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        // 2 names, 1 amount
        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", List.of("CAD", "CAD"),
                List.of(10.0));

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void execute_failsWhenMoreThanFiveCurrencies() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        List<String> names = List.of("CAD", "CAD", "CAD", "CAD", "CAD", "CAD");
        List<Double> amounts = List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", names, amounts);

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void execute_failsWhenHomeCurrencyUnknown() {
        Currency eur = new Currency("EUR", "€");

        RecordingPresenter presenter = new RecordingPresenter();
        // repo does NOT contain CAD
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(eur));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", List.of("EUR"), List.of(10.0));

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.contains("Unknown home currency"));
    }

    @Test
    void execute_failsWhenItemCurrencyUnknown() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        // repo does NOT contain EUR
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", List.of("EUR"), List.of(10.0));

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.contains("Unknown currency"));
    }

    @Test
    void execute_failsWhenDataAccessThrows() {
        Currency cad = new Currency("CAD", "$");
        Currency eur = new Currency("EUR", "€");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad, eur));
        ThrowingExchangeRates rates = new ThrowingExchangeRates();

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        TravelBudgetInputData input = new TravelBudgetInputData(
                "CAD", List.of("EUR"), List.of(10.0));

        interactor.execute(input);

        // Should hit the catch block and go to fail view.
        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
        assertTrue(presenter.lastError.startsWith("Travel budget error"));
    }

    @Test
    void switchToHomeView_callsPresenter() {
        Currency cad = new Currency("CAD", "$");

        RecordingPresenter presenter = new RecordingPresenter();
        FakeCurrencyRepository repo =
                new FakeCurrencyRepository(List.of(cad));
        FakeExchangeRates rates = new FakeExchangeRates(1.0);

        TravelBudgetInteractor interactor = makeInteractor(rates, repo, presenter);

        interactor.switchToHomeView();

        assertTrue(presenter.homeViewCalled);
    }
}

