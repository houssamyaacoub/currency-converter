package use_case.recent_currency;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RecentCurrencyInteractor}.
 *
 * These tests verify that usage is recorded correctly, that the top
 * frequent currencies are computed, and that the final ordered list
 * is assembled as expected.
 */
class RecentCurrencyInteractorTest {

    /**
     * Simple in-memory implementation of {@link RecentCurrencyDataAccessInterface}
     * used only for testing.
     *
     * It stores usage counts, favourites, and the list of all supported
     * currencies in memory. It also keeps track of which currencies
     * were recorded via {@link #recordUsage(String, String)} so that
     * tests can assert the correct behaviour.
     */
    private static class InMemoryRecentGateway implements RecentCurrencyDataAccessInterface {

        private final Map<String, Map<String, Integer>> usageByUser = new HashMap<>();
        private final Map<String, List<String>> favouritesByUser = new HashMap<>();
        private List<String> allSupported = new ArrayList<>();
        private final List<String> recordedCurrencies = new ArrayList<>();

        /**
         * Sets the initial usage counts for the given user id.
         *
         * @param userId the user id
         * @param usage  a map from currency code to usage count
         */
        void setUsage(String userId, Map<String, Integer> usage) {
            usageByUser.put(userId, new HashMap<>(usage));
        }

        /**
         * Sets the favourite currencies for the given user.
         *
         * @param userId     the user id
         * @param favourites list of favourite currency codes
         */
        void setFavourites(String userId, List<String> favourites) {
            favouritesByUser.put(userId, new ArrayList<>(favourites));
        }

        /**
         * Sets the global list of all supported currencies.
         *
         * @param allSupported a list of currency codes
         */
        void setAllSupported(List<String> allSupported) {
            this.allSupported = new ArrayList<>(allSupported);
        }

        /**
         * Returns the currencies that were recorded via {@link #recordUsage}.
         *
         * @return the recorded currencies in order of calls
         */
        List<String> getRecordedCurrencies() {
            return recordedCurrencies;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean userExists(String userId) {
            // In this test implementation we consider a user to "exist"
            // if they have any usage or favourites recorded.
            return usageByUser.containsKey(userId) || favouritesByUser.containsKey(userId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void recordUsage(String userId, String currencyCode) {
            recordedCurrencies.add(currencyCode);
            Map<String, Integer> usage = usageByUser.computeIfAbsent(userId, k -> new HashMap<>());
            usage.put(currencyCode, usage.getOrDefault(currencyCode, 0) + 1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Integer> getUsageCounts(String userId) {
            return new HashMap<>(usageByUser.getOrDefault(userId, new HashMap<>()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getFavouriteCurrencies(String userId) {
            return new ArrayList<>(favouritesByUser.getOrDefault(userId, new ArrayList<>()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getAllSupportedCurrencies() {
            return new ArrayList<>(allSupported);
        }

        /**
         * {@inheritDoc}
         *
         * For this in-memory implementation we do not need to use this method
         * inside the tests, so it can simply delegate to a default behaviour
         * (for example, favourites followed by all supported).
         */
        @Override
        public List<String> getOrderedCurrenciesForUser(String userId) {
            List<String> favourites = getFavouriteCurrencies(userId);
            List<String> result = new ArrayList<>(favourites);

            for (String code : allSupported) {
                if (!result.contains(code)) {
                    result.add(code);
                }
            }
            return result;
        }
    }

    /**
     * Test that a successful call records usage and builds the correct ordered list.
     */
    @Test
    void success_recordsUsageAndBuildsOrderedList() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";

        gateway.setUsage(userId, Map.of(
                "CAD", 3,
                "USD", 1
        ));
        gateway.setFavourites(userId, List.of("EUR"));
        gateway.setAllSupported(List.of("CAD", "USD", "EUR", "JPY"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // user id is propagated
                assertEquals(userId, outputData.getUserId());

                // favourites should come first
                assertEquals(List.of("EUR"), outputData.getFavouriteCurrencies());

                // top frequent should be computed based on usage counts
                List<String> topFrequent = outputData.getTopFrequentCurrencies();
                assertTrue(topFrequent.contains("CAD"));
                assertTrue(topFrequent.contains("USD"));

                // ordered list: favourites -> top frequent -> remaining supported
                List<String> ordered = outputData.getOrderedCurrencyList();
                assertEquals("EUR", ordered.get(0));
                assertTrue(ordered.indexOf("CAD") < ordered.indexOf("USD"));
                assertTrue(ordered.contains("JPY"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "CAD", "USD");

        // Act
        interactor.execute(input);

        // Assert: usage was recorded for both currencies
        assertEquals(List.of("CAD", "USD"), gateway.getRecordedCurrencies());
    }

    /**
     * Test that a missing or blank user id results in a failure.
     */
    @Test
    void failure_userNotLoggedIn() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected when user id is empty.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("User is not logged in.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData("", "CAD", "USD");

        // Act
        interactor.execute(input);
    }

    /**
     * Test that a request for a non-existent user fails with a clear message.
     */
    @Test
    void failure_userDoesNotExist() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "nonexistent";

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected for a non-existent user.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("User does not exist.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "CAD", "USD");

        // Act
        interactor.execute(input);
    }

    /**
     * When both "from" and "to" currency codes are blank, the interactor should
     * not record any new usage but still succeed by computing the ordered list
     * from existing usage, favourites and supported currencies.
     */
    @Test
    void success_refreshOrderingWithoutRecordingUsage_whenNoValidCurrenciesProvided() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";

        // Pre-populate some usage counts and favourites so we can verify ordering.
        Map<String, Integer> initialUsage = new HashMap<>();
        initialUsage.put("CAD", 5);
        initialUsage.put("USD", 3);
        gateway.setUsage(userId, initialUsage);

        gateway.setFavourites(userId, List.of("EUR"));
        gateway.setAllSupported(List.of("CAD", "USD", "EUR", "JPY"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // Assert basic output data
                assertEquals(userId, outputData.getUserId());
                assertEquals(List.of("EUR"), outputData.getFavouriteCurrencies());

                // Usage counts in the gateway should remain unchanged
                Map<String, Integer> finalUsage = gateway.getUsageCounts(userId);
                assertEquals(Integer.valueOf(5), finalUsage.get("CAD"));
                assertEquals(Integer.valueOf(3), finalUsage.get("USD"));

                // Top frequent should reflect the existing usage, limited to the top few
                assertEquals(List.of("CAD", "USD"), outputData.getTopFrequentCurrencies());

                // Ordered list should place favourites first, then top frequent, then the rest
                assertEquals(List.of("EUR", "CAD", "USD", "JPY"), outputData.getOrderedCurrencyList());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected when only refreshing ordering: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Both "from" and "to" are blank: this should trigger a "refresh only" call.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "   ", "   ");

        // Act
        interactor.execute(input);

        // Assert: no new usage should be recorded.
        assertTrue(gateway.getRecordedCurrencies().isEmpty());
    }

    /**
     * Test that the list of top frequent currencies is limited to at most 5 entries.
     */
    @Test
    void topFrequentLimitedToAtMostFive() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";

        Map<String, Integer> usage = new LinkedHashMap<>();
        usage.put("C1", 10);
        usage.put("C2", 9);
        usage.put("C3", 8);
        usage.put("C4", 7);
        usage.put("C5", 6);
        usage.put("C6", 5);
        usage.put("C7", 4);
        gateway.setUsage(userId, usage);

        gateway.setFavourites(userId, Collections.emptyList());
        gateway.setAllSupported(new ArrayList<>(usage.keySet()));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                List<String> topFrequent = outputData.getTopFrequentCurrencies();

                // At most 5 entries should be returned.
                assertTrue(topFrequent.size() <= 5);

                // The most frequently used currencies should be included.
                assertTrue(topFrequent.contains("C1"));
                assertTrue(topFrequent.contains("C2"));
                assertTrue(topFrequent.contains("C3"));
                assertTrue(topFrequent.contains("C4"));
                assertTrue(topFrequent.contains("C5"));

                // The less-used currencies should not appear in the top list.
                assertFalse(topFrequent.contains("C6"));
                assertFalse(topFrequent.contains("C7"));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Using a valid pair of currencies so that usage recording is allowed.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "C1", "C2");

        // Act
        interactor.execute(input);
    }
}
