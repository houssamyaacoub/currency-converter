package use_case.recent_currency;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.lang.reflect.Method;




/**
 * Unit tests for {@link RecentCurrencyInteractor}.
 * These tests verify that usage is recorded correctly, that the top
 * frequent currencies are computed, and that the final ordered list
 * is assembled as expected.
 */
class RecentCurrencyInteractorTest {

    /**
     * Simple in-memory implementation of {@link RecentCurrencyDataAccessInterface}
     * used only for testing.
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
     * Test double for {@link RecentCurrencyDataAccessInterface} that deliberately
     * returns null for all collection-returning methods. This allows us to hit
     * the defensive null-handling branches inside the interactor.
     */
    private static class NullCollectionsGateway implements RecentCurrencyDataAccessInterface {

        private String lastRecordedCurrency;

        @Override
        public boolean userExists(String userId) {
            return true;
        }

        @Override
        public void recordUsage(String userId, String currencyCode) {
            // Remember the last recorded currency so that tests can verify
            // that recordUsage(...) was actually called.
            this.lastRecordedCurrency = currencyCode;
        }

        @Override
        public Map<String, Integer> getUsageCounts(String userId) {
            // Return null on purpose to trigger the defensive code path.
            return null;
        }

        @Override
        public List<String> getFavouriteCurrencies(String userId) {
            // Return null on purpose to trigger the defensive code path.
            return null;
        }

        @Override
        public List<String> getAllSupportedCurrencies() {
            // Return null on purpose to trigger the defensive code path.
            return null;
        }

        @Override
        public List<String> getOrderedCurrenciesForUser(String userId) {
            // Not used in these specific tests; we can safely return an empty list.
            return List.of();
        }

        /**
         * Returns the last currency code that was passed to recordUsage(...).
         *
         * @return the last recorded currency code, or null if none was recorded.
         */
        String getLastRecordedCurrency() {
            return lastRecordedCurrency;
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
     * When both the "from" and "to" currency codes are {@code null}, the interactor
     * should treat this as "no valid currencies provided", not record any usage,
     * and still succeed by computing an ordered list from favourites and the
     * supported currencies when there are no existing usage counts.
     */
    @Test
    void success_handlesNullCurrencyCodesAndEmptyUsage() {
        // Arrange
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "user-null";

        // Mark the user as existing by giving them at least one favourite currency.
        // We deliberately do NOT set any usage counts so that the usage map for this
        // user remains empty.
        gateway.setFavourites(userId, List.of("EUR"));
        gateway.setAllSupported(List.of("EUR", "CAD"));

        // Presenter that will assert the expected values on a successful execution.
        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // Basic sanity checks for the output data.
                assertEquals(userId, outputData.getUserId(),
                        "The user id in the output should match the input user id.");
                assertEquals(List.of("EUR"), outputData.getFavouriteCurrencies(),
                        "Favourites in the output should match the favourites in the gateway.");

                // Because no usage has ever been recorded for this user, the list of
                // top frequent currencies should be empty.
                assertTrue(outputData.getTopFrequentCurrencies().isEmpty(),
                        "Top frequent currencies should be empty when there is no usage.");

                // The ordered list should still be computed. It should contain the
                // favourites first, followed by the remaining supported currencies.
                assertEquals(List.of("EUR", "CAD"), outputData.getOrderedCurrencyList(),
                        "Ordered list should be favourites followed by remaining supported currencies.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // This path should not be reached for this scenario.
                fail("Failure is not expected when both currency codes are null: " + errorMessage);
            }
        };

        // Create the interactor under test with the in-memory gateway and the presenter.
        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Provide null for both the "from" and "to" currency codes.
        // This forces the interactor to exercise the branch where normalize(...)
        // receives a null value and returns null.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, null, null);

        // Act
        interactor.execute(input);

        // Assert: because both currency codes were null, no usage should have
        // been recorded in the gateway. This also ensures the branch where the
        // usage map is empty is covered.
        assertTrue(gateway.getRecordedCurrencies().isEmpty(),
                "No currencies should be recorded when both input codes are null.");
    }

    /**
     * RecentCurrencyOutputData: when the constructor receives null lists,
     * it should replace them with empty lists instead of keeping null.
     */
    @Test
    void outputDataUsesEmptyListsWhenArgumentsAreNull() {
        RecentCurrencyOutputData data =
                new RecentCurrencyOutputData("user1", null, null, null);

        assertEquals("user1", data.getUserId());

        assertNotNull(data.getFavouriteCurrencies());
        assertTrue(data.getFavouriteCurrencies().isEmpty());

        assertNotNull(data.getTopFrequentCurrencies());
        assertTrue(data.getTopFrequentCurrencies().isEmpty());

        assertNotNull(data.getOrderedCurrencyList());
        assertTrue(data.getOrderedCurrencyList().isEmpty());
    }

    /**
     * RecentCurrencyOutputData: getters should return defensive,
     * unmodifiable copies of the internal lists.
     */
    @Test
    void outputDataGettersReturnDefensiveUnmodifiableCopies() {
        List<String> favourites = new ArrayList<>();
        favourites.add("EUR");

        List<String> top = new ArrayList<>();
        top.add("CAD");

        List<String> ordered = new ArrayList<>();
        ordered.add("EUR");
        ordered.add("CAD");

        RecentCurrencyOutputData data =
                new RecentCurrencyOutputData("user2", favourites, top, ordered);

        // Change the original lists; this must NOT affect the data object.
        favourites.add("USD");
        top.add("USD");
        ordered.clear();

        assertEquals(List.of("EUR"), data.getFavouriteCurrencies());
        assertEquals(List.of("CAD"), data.getTopFrequentCurrencies());
        assertEquals(List.of("EUR", "CAD"), data.getOrderedCurrencyList());

        // The returned lists should be unmodifiable.
        assertThrows(UnsupportedOperationException.class,
                () -> data.getFavouriteCurrencies().add("GBP"));
    }

    /**
     * When the gateway returns null for usage counts, favourites and all supported
     * currencies, the interactor should treat them as empty collections and still
     * produce a successful result without throwing any exceptions.
     * This test covers the branches where the interactor checks for null collections.
     */
    @Test
    void executeHandlesNullCollectionsFromGatewayGracefully() {
        String userId = "null-collections-user";
        NullCollectionsGateway gateway = new NullCollectionsGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                assertEquals(userId, outputData.getUserId());

                // All lists should be non-null but empty.
                assertNotNull(outputData.getFavouriteCurrencies());
                assertTrue(outputData.getFavouriteCurrencies().isEmpty());

                assertNotNull(outputData.getTopFrequentCurrencies());
                assertTrue(outputData.getTopFrequentCurrencies().isEmpty());

                assertNotNull(outputData.getOrderedCurrencyList());
                assertTrue(outputData.getOrderedCurrencyList().isEmpty());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is not expected when gateway collections are null: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Only "from" is a valid currency; "to" is null. This still represents
        // a valid usage and should be recorded.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "CAD", null);

        interactor.execute(input);

        // Verify that recordUsage(...) was invoked at least once.
        assertEquals("CAD", gateway.getLastRecordedCurrency());
    }

    /**
     * When favourites and all supported currencies contain null or blank entries,
     * the interactor should ignore those entries after normalisation and should
     * not include them in the final ordered currency list.
     * This test covers the branches where normalize(...) returns null and the
     * "if (normalized != null)" checks inside the ordered list builder.
     */
    @Test
    void executeIgnoresNullAndBlankEntriesFromGatewayLists() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "messy-user";

        // Provide some initial usage so that the top frequent list is non-empty.
        gateway.setUsage(userId, Map.of("USD", 2));

        // Favourites contain only valid, non-null entries so that
        // RecentCurrencyOutputData can safely copy them.
        gateway.setFavourites(userId, List.of("EUR"));

        // All supported currencies contain null and blank entries on purpose.
        // These will be passed into buildOrderedList(...), where normalize(...)
        // should return null for the invalid entries and they should be skipped.
        gateway.setAllSupported(Arrays.asList("EUR", "USD", null, "   "));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // Favourites should remain as configured in the gateway.
                assertEquals(List.of("EUR"), outputData.getFavouriteCurrencies());

                // Ordered list should contain only the valid, non-blank entries,
                // with no duplicates.
                List<String> ordered = outputData.getOrderedCurrencyList();
                assertEquals(List.of("EUR", "USD"), ordered);
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is not expected in this scenario: " + errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Only "from" is valid; "to" is null. This still should be recorded,
        // but the focus of this test is on how the ordered list is built
        // from favourites and all supported currencies.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, "USD", null);

        interactor.execute(input);
    }

    /**
     * Extra branch coverage: tests normalize("") and single-sided recording of usage.
     */
    @Test
    void branchCoverage_normalizeBlankAndSingleSideRecording() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "branch-user";

        // Make user "exist"
        gateway.setFavourites(userId, List.of("EUR"));
        gateway.setAllSupported(List.of("EUR", "USD"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // We only assert the call does not fail; details not needed for branch coverage.
                assertEquals(userId, outputData.getUserId());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Unexpected failure: " + errorMessage);
            }
        };

        RecentCurrencyInteractor interactor = new RecentCurrencyInteractor(gateway, presenter);

        // Case 1: "from" is blank → normalize("") returns null
        RecentCurrencyInputData input1 =
                new RecentCurrencyInputData(userId, "   ", "USD");
        interactor.execute(input1);
        assertEquals(List.of("USD"), gateway.getRecordedCurrencies());

        // Clear recorded usage to test other branch
        gateway.getRecordedCurrencies().clear();

        // Case 2: "to" is blank → normalize("") hits empty branch again
        RecentCurrencyInputData input2 =
                new RecentCurrencyInputData(userId, "USD", " ");
        interactor.execute(input2);
        assertEquals(List.of("USD"), gateway.getRecordedCurrencies());
    }

    /**
     * Branch coverage test: covers the case where usageCounts is empty,
     * and favourites/topFrequent/allSupported are empty lists (not null).
     */
    @Test
    void branchCoverage_emptyCollectionsNotNull() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "branch-empty";

        // Mark user as existing
        gateway.setFavourites(userId, List.of());
        gateway.setAllSupported(List.of());

        // No usage set → usageCounts = empty map
        // topFrequent = computeTopFrequent(empty map) → empty list

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // All lists should be empty (but NOT null)
                assertNotNull(outputData.getFavouriteCurrencies());
                assertTrue(outputData.getFavouriteCurrencies().isEmpty());

                assertNotNull(outputData.getTopFrequentCurrencies());
                assertTrue(outputData.getTopFrequentCurrencies().isEmpty());

                assertNotNull(outputData.getOrderedCurrencyList());
                assertTrue(outputData.getOrderedCurrencyList().isEmpty());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Unexpected failure: " + errorMessage);
            }
        };

        RecentCurrencyInteractor interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Both currency codes blank → normalize returns null → no recording
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, " ", " ");

        interactor.execute(input);
    }

    /**
     * Forces normalize() inside favourites and topFrequent loops
     * to hit all three branches: null, blank, and valid strings.
     * This test only cares that valid entries appear in the final list.
     */
    @Test
    void branchCoverage_normalizeInsideBuildOrderedList() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "normalize-branches";

        // User exists
        gateway.setUsage(userId, Map.of(
                "cad", 3   // this will appear in topFrequent
        ));

        // favourites include:
        // null  -> normalize(null)  -> null (branch A)
        // " "   -> normalize(" ")   -> null (branch B)
        // "eur" -> normalize("eur") -> "eur" (branch C)
        gateway.setFavourites(userId, Arrays.asList(null, " ", "eur"));

        // allSupported contains mixed case values; these will exercise
        // normalize(...) in the allSupported loop as well.
        gateway.setAllSupported(List.of("CAD", "USD", "EUR"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                List<String> ordered = outputData.getOrderedCurrencyList();

                // Basic sanity checks for branch coverage.
                assertFalse(ordered.isEmpty());

                // The valid values from favourites and topFrequent should appear.
                assertTrue(ordered.contains("eur"));
                assertTrue(ordered.contains("cad"));

                // There should be at least one entry corresponding to "USD",
                // regardless of case.
                assertTrue(ordered.stream().anyMatch(code -> code.equalsIgnoreCase("usd")));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail(errorMessage);
            }
        };

        RecentCurrencyInteractor interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Only provide blank currency codes (no usage recorded here);
        // we are focusing on the ordering logic.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, " ", " ");

        interactor.execute(input);
    }

    /**
     * Branch coverage helper: directly invokes the private buildOrderedList(...)
     * method via reflection so we can cover the branches where:
     *  - topFrequent is null (if(topFrequent != null) == false), and
     *  - entries inside topFrequent normalise to null or non-null.
     */
    @Test
    void branchCoverage_buildOrderedListTopFrequentBranches() throws Exception {
        // Gateway / presenter are not used by buildOrderedList, but we need
        // a concrete interactor instance to invoke the private method on.
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // Not used in this test.
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("buildOrderedList branch coverage test should not call presenter.");
            }
        };

        RecentCurrencyInteractor interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // Obtain a handle to the private buildOrderedList(...) method.
        Method buildMethod = RecentCurrencyInteractor.class.getDeclaredMethod(
                "buildOrderedList", List.class, List.class, List.class);
        buildMethod.setAccessible(true);

        // ---- Case 1: topFrequent is null -> if (topFrequent != null) false branch ----
        @SuppressWarnings("unchecked")
        List<String> result1 = (List<String>) buildMethod.invoke(
                interactor,
                List.of("EUR"),   // favourites
                null,             // topFrequent is null on purpose
                List.of("CAD"));  // allSupported

        // Favourites first, then remaining supported currencies.
        assertEquals(List.of("EUR", "CAD"), result1);

        // ---- Case 2: topFrequent contains null / blank / valid entries ----
        List<String> messyTop = new ArrayList<>();
        messyTop.add(null);   // normalize -> null
        messyTop.add(" ");    // normalize -> null
        messyTop.add("usd");  // normalize -> "usd"

        @SuppressWarnings("unchecked")
        List<String> result2 = (List<String>) buildMethod.invoke(
                interactor,
                List.of(),       // no favourites
                messyTop,        // topFrequent with mixed entries
                List.of());      // no allSupported

        // After normalisation, only the valid non-null entry should remain.
        assertEquals(List.of("usd"), result2);
    }

    /**
     * When the user id itself is null, the interactor should treat this
     * as "not logged in" and fail with the same error message.
     */
    @Test
    void failure_userIdIsNull() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                fail("Success not expected when user id is null.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("User is not logged in.", errorMessage);
            }
        };

        RecentCurrencyInputBoundary interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        // userId is null on purpose to hit the "userId == null" branch.
        RecentCurrencyInputData input =
                new RecentCurrencyInputData(null, "CAD", "USD");

        interactor.execute(input);
    }

    /**
     * RecentCurrencyOutputData: if the orderedCurrencyList argument contains
     * only null values, it should become an empty list after construction.
     */
    @Test
    void outputDataTreatsAllNullOrderedListAsEmpty() {
        List<String> favourites = List.of("CAD");
        List<String> top = List.of("USD");

        List<String> ordered = new ArrayList<>();
        ordered.add(null);
        ordered.add(null);

        RecentCurrencyOutputData data =
                new RecentCurrencyOutputData("user4", favourites, top, ordered);

        // Favourites and top should be preserved.
        assertEquals(List.of("CAD"), data.getFavouriteCurrencies());
        assertEquals(List.of("USD"), data.getTopFrequentCurrencies());

        // Ordered list had only nulls -> becomes empty after filtering.
        assertTrue(data.getOrderedCurrencyList().isEmpty());
    }




    /**
     * RecentCurrencyOutputData: a null userId should be converted
     * to an empty string instead of being kept as null.
     */
    @Test
    void outputDataTreatsNullUserIdAsEmptyString() {
        RecentCurrencyOutputData data =
                new RecentCurrencyOutputData(null,
                        List.of("CAD"),
                        List.of(),
                        List.of());

        assertEquals("", data.getUserId());
        assertEquals(List.of("CAD"), data.getFavouriteCurrencies());
    }


    /**
     * RecentCurrencyOutputData: lists that contain only null elements should
     * be treated as empty lists after construction.
     */
    @Test
    void outputDataTreatsAllNullElementsAsEmptyLists() {
        // Use explicit lists instead of Arrays.asList(null) to avoid
        // passing a null array into Arrays.asList, which would throw NPE.
        List<String> favourites = new ArrayList<>();
        favourites.add(null);
        favourites.add(null);

        List<String> top = new ArrayList<>();
        top.add(null);

        List<String> ordered = new ArrayList<>();
        ordered.add(null);
        ordered.add("CAD");
        ordered.add(null);

        RecentCurrencyOutputData data =
                new RecentCurrencyOutputData("user3", favourites, top, ordered);

        // Favourites and top frequent contain only nulls -> become empty lists.
        assertTrue(data.getFavouriteCurrencies().isEmpty());
        assertTrue(data.getTopFrequentCurrencies().isEmpty());

        // Ordered contains one valid entry; nulls should be removed.
        assertEquals(List.of("CAD"), data.getOrderedCurrencyList());
    }


    /**
     * Branch coverage: ensures computeTopFrequent covers equal usage count path.
     */
    @Test
    void branchCoverage_computeTopFrequent_equalValues() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "equal-usage";

        // two equal usage values → triggers e2 == e1 case in comparator
        gateway.setUsage(userId, Map.of(
                "AAA", 5,
                "BBB", 5
        ));

        gateway.setFavourites(userId, List.of());
        gateway.setAllSupported(List.of("AAA","BBB"));

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                // order doesn't matter as long as no crash
                assertEquals(2, outputData.getTopFrequentCurrencies().size());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail(errorMessage);
            }
        };

        RecentCurrencyInteractor interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        RecentCurrencyInputData input =
                new RecentCurrencyInputData(userId, " ", " "); // no usage recorded

        interactor.execute(input);
    }

    /**
     * Branch coverage: ensures buildOrderedList covers empty topFrequent list.
     */
    @Test
    void branchCoverage_emptyTopFrequentList() {
        InMemoryRecentGateway gateway = new InMemoryRecentGateway();
        String userId = "empty-top";

        gateway.setUsage(userId, Map.of()); // ensures topFrequent = empty list
        gateway.setFavourites(userId, List.of());
        gateway.setAllSupported(List.of());

        RecentCurrencyOutputBoundary presenter = new RecentCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentCurrencyOutputData outputData) {
                assertTrue(outputData.getTopFrequentCurrencies().isEmpty());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail(errorMessage);
            }
        };

        RecentCurrencyInteractor interactor =
                new RecentCurrencyInteractor(gateway, presenter);

        interactor.execute(new RecentCurrencyInputData(userId, "", ""));
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
