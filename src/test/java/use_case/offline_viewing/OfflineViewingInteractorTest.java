package use_case.offline_viewing;

import data_access.offline_viewing.PairRateCache;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for use case: OfflineViewInteractor.
 *
 * We only test the interactor here. We stub:
 *  - PairRateCache (so we control the cached rates and timestamps), and
 *  - OfflineViewOutputBoundary (so we can assert success/failure behaviour).
 */
class OfflineViewingInteractorTest {

    /**
     * Simple stub over PairRateCache.
     *
     * We ignore the actual file and just return the data we inject through
     * the constructor. This avoids touching real offline data files.
     */
    private static class StubPairRateCache extends PairRateCache {

        private final Map<String, Double> rates;
        private final Instant latestTimestamp;
        private final boolean throwOnAccess;

        StubPairRateCache(Map<String, Double> rates,
                          Instant latestTimestamp,
                          boolean throwOnAccess) {
            super("test_pairs_stub.csv");
            this.rates = rates;
            this.latestTimestamp = latestTimestamp;
            this.throwOnAccess = throwOnAccess;
        }

        @Override
        public synchronized Map<String, Double> getAllRates() {
            if (this.throwOnAccess) {
                throw new RuntimeException("Simulated cache failure");
            }
            return this.rates;
        }

        @Override
        public synchronized Instant getLatestTimestamp() {
            if (this.throwOnAccess) {
                throw new RuntimeException("Simulated cache failure");
            }
            return this.latestTimestamp;
        }
    }

    @Test
    void successTest_validOfflineData() {
        // --- Arrange ---
        Map<String, Double> cachedRates = new HashMap<>();
        cachedRates.put("USD->CAD", 1.35);
        cachedRates.put("EUR->CAD", 1.47);
        Instant latest = Instant.now();

        PairRateCache cache = new StubPairRateCache(
                cachedRates, latest, false);

        final boolean[] presentCalled = {false};
        final boolean[] failCalled = {false};

        OfflineViewOutputBoundary presenter = new OfflineViewOutputBoundary() {
            @Override
            public void present(OfflineViewOutputData data) {
                presentCalled[0] = true;
                assertNotNull(data,
                        "Output data should not be null on success.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
            }
        };

        OfflineViewInteractor interactor =
                new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertTrue(presentCalled[0],
                "present should be called when valid offline data exists.");
        assertFalse(failCalled[0],
                "prepareFailView should not be called on success.");
    }

    @Test
    void failureTest_emptyRates() {
        // --- Arrange ---
        Map<String, Double> emptyRates = Collections.emptyMap();
        Instant latest = Instant.now();

        PairRateCache cache = new StubPairRateCache(
                emptyRates, latest, false);

        final boolean[] presentCalled = {false};
        final boolean[] failCalled = {false};
        final String[] errorMsg = {null};

        OfflineViewOutputBoundary presenter = new OfflineViewOutputBoundary() {
            @Override
            public void present(OfflineViewOutputData data) {
                presentCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                errorMsg[0] = errorMessage;
            }
        };

        OfflineViewInteractor interactor =
                new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertFalse(presentCalled[0],
                "present should not be called when no rates exist.");
        assertTrue(failCalled[0],
                "prepareFailView should be called when offline data is "
                        + "unavailable.");
        assertEquals("Offline data unavailable.", errorMsg[0]);
    }

    @Test
    void failureTest_exceptionFromCache() {
        // --- Arrange ---
        Map<String, Double> someRates = new HashMap<>();
        someRates.put("USD->CAD", 1.35);

        PairRateCache cache = new StubPairRateCache(
                someRates, Instant.now(), true);

        final boolean[] presentCalled = {false};
        final boolean[] failCalled = {false};
        final String[] errorMsg = {null};

        OfflineViewOutputBoundary presenter = new OfflineViewOutputBoundary() {
            @Override
            public void present(OfflineViewOutputData data) {
                presentCalled[0] = true;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
                errorMsg[0] = errorMessage;
            }
        };

        OfflineViewInteractor interactor =
                new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertFalse(presentCalled[0],
                "present should not be called when the cache throws.");
        assertTrue(failCalled[0],
                "prepareFailView should be called when an exception occurs.");
        assertEquals("Offline data unavailable.", errorMsg[0]);
    }
}
