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
         * Sets the favourite currencies for the given user id.
         *
         * @param userId     the user id
         * @param favourites the list of favourite currency codes
         */
        void setFavourites(String userId, List<String> favourites) {
            favouritesByUser.put(userId, new ArrayList<>(favourites));
        }

        /**
         * Sets the list of all supported currency codes.
         *
         * @param allSupported the list of all supported currencies
         */
        void setAllSupported(List<String> allSupported) {
            this.allSupported = new ArrayList<>(allSupported);
        }

        /**
         * Returns a list of the currency codes that were passed to
         * {@link #recordUsage(String, String)}.
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
            Map<String, Integer> usage = usageByUser.computeIfAbsent(userId, u -> new HashMap<>());
            usage.put(currencyCode, usage.getOrDefault(currencyCode, 0) + 1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Integer> getUsageCounts(String userId) {
            return usageByUser.getOrDefault(userId, new HashMap<>());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getFavouriteCurrencies(String userId) {
            return favouritesByUser.getOrDefault(userId, new ArrayList<>());
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
         * The interactor does not rely on this method in our current tests,
         * so we simply return {@link #getAllSupportedCurrencies()}.
         */
        @Override
        public List<String> getOrderedCurrenciesForUser(String userId) {
            return getAllSupportedCurrencies();
        }
    }

    /**
     * Test a full successful scenario where:
     *
     * <ul>
     *   <li>Existing usage counts are present.</li>
     *   <li>Favourites are defined for the user.</li>
     *   <li>The interactor records usage for the "from" and "to" currencies.</li>
     *   <li>Top frequent currencies and ordered list are computed correctly.</li>
     * </ul>
     */
    @Test
    void success_recordsUsageAndBuildsOrderedList() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";

        // Initial usage: CAD is used the most, then USD, then JPY.
        Map<String, Integer> initialUsage = new HashMap<>();
        initialUsage.put("CAD", 5);
        initialUsage.put("USD", 3);
        initialUsage.put("JPY", 1);
        gateway.setUsage(userId, initialUsage);

        // Favourites for this user.
        gateway.setFavourites(userId, List.of("EUR", "CAD"));

        // All supported currencies in the system.
        gateway.setAllSupported(List.of("CAD", "USD", "EUR", "JPY", "GBP"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // Assert basic fields.
                assertEquals(userId, outputData.getUserId());
                assertEquals(List.of("EUR", "CAD"), outputData.getFavouriteCurrencies());

                // After execute, USD and JPY usage counts should each be incremented by 1.
                Map<String, Integer> finalUsage = gateway.getUsageCounts(userId);
                assertEquals(Integer.valueOf(5), finalUsage.get("CAD"));
                assertEquals(Integer.valueOf(4), finalUsage.get("USD"));
                assertEquals(Integer.valueOf(2), finalUsage.get("JPY"));

                // Top frequent currencies, sorted by usage (descending).
                assertEquals(List.of("CAD", "USD", "JPY"),
                        outputData.getTopFrequentCurrencies());

                // Final ordered list: favourites first, then top frequent, then remaining currencies.
                assertEquals(List.of("EUR", "CAD", "USD", "JPY", "GBP"),
                        outputData.getOrderedCurrencyList());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // NOTE: We intentionally include spaces around the codes to test trimming.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, " USD ", " JPY ");

        // Act
        interactor.execute(input);

        // Assert: recordUsage was called twice with trimmed codes.
        assertEquals(List.of("USD", "JPY"), gateway.getRecordedCurrencies());
    }

    /**
     * Test that an empty user id is treated as "user not logged in" and
     * that {@code prepareFailView} is called with the correct message.
     */
    @Test
    void failure_userNotLoggedIn() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in RecentCurrencyInteractor.
                assertEquals("User is not logged in.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData("", "CAD", "USD");

        // Act
        interactor.execute(input);

        // Assert: no usage should have been recorded.
        assertTrue(gateway.getRecordedCurrencies().isEmpty());
    }

    /**
     * Test that a non-existing user id produces a failure view
     * with the expected error message.
     */
    @Test
    void failure_userDoesNotExist() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in RecentCurrencyInteractor.
                assertEquals("User does not exist.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData("ghost", "CAD", "USD");

        // Act
        interactor.execute(input);
    }

    /**
     * Test that when both "from" and "to" currencies are blank or invalid,
     * the interactor fails with the "No valid currencies provided." message
     * and does not record any usage.
     */
    @Test
    void failure_noValidCurrenciesProvided() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";
        gateway.setUsage(userId, new HashMap<>());

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in RecentCurrencyInteractor.
                assertEquals("No valid currencies provided.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Both "from" and "to" are blank.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "   ", "   ");

        // Act
        interactor.execute(input);

        // Assert: nothing should be recorded.
        assertTrue(gateway.getRecordedCurrencies().isEmpty());
    }

    /**
     * Test that only the top 5 most frequently used currencies are returned
     * in {@link RecentCurrencyOutputData#getTopFrequentCurrencies()}, even if
     * there are more than 5 currencies in the usage map.
     */
    @Test
    void topFrequentLimitedToAtMostFive() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user1";

        Map<String, Integer> usage = new HashMap<>();
        usage.put("C1", 10);
        usage.put("C2", 9);
        usage.put("C3", 8);
        usage.put("C4", 7);
        usage.put("C5", 6);
        usage.put("C6", 5);
        usage.put("C7", 4);
        gateway.setUsage(userId, usage);

        gateway.setFavourites(userId, Collections.emptyList());
        gateway.setAllSupported(List.of("C1", "C2", "C3", "C4", "C5", "C6", "C7"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // MAX_TOP_FREQUENT is 5 in the interactor.
                assertEquals(5, outputData.getTopFrequentCurrencies().size());
                assertTrue(outputData.getTopFrequentCurrencies().containsAll(
                        List.of("C1", "C2", "C3", "C4", "C5")));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "C1", null);

        // Act
        interactor.execute(input);
    }
}
