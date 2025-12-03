package use_case.offline_viewing;

import data_access.offline_viewing.PairRateCache;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Use Case: OfflineViewInteractor.
 *
 * We only test the interactor here. We use:
 *  - real PairRateCache instances (with test filenames) or small subclasses
 *    to control the behaviour of getAllRates / getLatestTimestamp
 *  - a stub OfflineViewOutputBoundary so we can assert success/failure
 */
class OfflineViewingInteractorTest {

    @Test
    void successTest_validOfflineData() {
        // --- Arrange ---
        // Use a test-specific filename so we don't interfere with real app data.
        PairRateCache cache = new PairRateCache("test_pairs_success.csv");

        // Put a couple of fake pairs into the cache. This will also write to disk,
        // but only to our test file.
        Instant ts1 = Instant.now().minusSeconds(60);
        Instant ts2 = Instant.now();
        cache.put("USD", "CAD", 1.35, ts1);
        cache.put("EUR", "CAD", 1.47, ts2);

        final boolean[] presentCalled = {false};
        final boolean[] failCalled = {false};

        OfflineViewOutputBoundary presenter = new OfflineViewOutputBoundary() {
            @Override
            public void present(OfflineViewOutputData data) {
                presentCalled[0] = true;
                assertNotNull(data, "Output data should not be null on success.");
                // If OfflineViewOutputData has getters, you COULD do:
                // assertEquals(cache.getAllRates(), data.getRates());
                // assertEquals(cache.getLatestTimestamp(), data.getLatestTimestamp());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
            }
        };

        OfflineViewInteractor interactor = new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertTrue(presentCalled[0], "present should be called when valid offline data exists.");
        assertFalse(failCalled[0], "prepareFailView should not be called on success.");
    }

    @Test
    void failureTest_noOfflineData() {
        // --- Arrange ---
        // New cache with a fresh test file: no pairs -> empty map + null latest timestamp.
        PairRateCache cache = new PairRateCache("test_pairs_empty.csv");

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

        OfflineViewInteractor interactor = new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertFalse(presentCalled[0], "present should not be called when cache is empty.");
        assertTrue(failCalled[0], "prepareFailView should be called when offline data is unavailable.");
        assertEquals("Offline data unavailable.", errorMsg[0]);
    }

    @Test
    void failureTest_exceptionFromCache() {
        // --- Arrange ---
        // Anonymous subclass that throws when getAllRates is called.
        PairRateCache cache = new PairRateCache("test_pairs_exception.csv") {
            @Override
            public Map<String, Double> getAllRates() {
                throw new RuntimeException("Simulated cache failure");
            }

            @Override
            public Instant getLatestTimestamp() {
                // Won't actually be used (getAllRates throws first),
                // but we must still provide an implementation.
                return Instant.now();
            }
        };

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

        OfflineViewInteractor interactor = new OfflineViewInteractor(cache, presenter);

        // --- Act ---
        interactor.execute();

        // --- Assert ---
        assertFalse(presentCalled[0],
                "present should not be called when the cache throws an exception.");
        assertTrue(failCalled[0],
                "prepareFailView should be called when an exception occurs.");
        assertEquals("Offline data unavailable.", errorMsg[0]);
    }
}
