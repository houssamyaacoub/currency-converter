package use_case.favourite_currency;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FavouriteCurrencyInteractor}.
 *
 * These tests use an in-memory implementation of
 * {@link FavouriteCurrencyDataAccessInterface} so that we can test
 * the interactor logic without touching the file system or database.
 */
class FavouriteCurrencyInteractorTest {

    /**
     * Simple in-memory implementation of {@link FavouriteCurrencyDataAccessInterface}
     * used only for testing.
     *
     * It stores favourites in a {@link HashMap} from user id to a list of
     * favourite currency codes.
     */
    private static class InMemoryFavouriteGateway implements FavouriteCurrencyDataAccessInterface {

        private final Map<String, List<String>> favouritesByUser = new HashMap<>();

        /**
         * Sets the favourites for the given user id.
         *
         * @param userId     the user id
         * @param favourites the list of favourite currency codes
         */
        void setFavourites(String userId, List<String> favourites) {
            favouritesByUser.put(userId, new ArrayList<>(favourites));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean userExists(String userId) {
            return favouritesByUser.containsKey(userId);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean currencyExists(String currencyCode) {
            // In tests we only care that the code is non-null and not blank.
            return currencyCode != null && !currencyCode.trim().isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getFavouritesForUser(String userId) {
            return new ArrayList<>(favouritesByUser.getOrDefault(userId, new ArrayList<>()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveFavouritesForUser(String userId, List<String> favourites) {
            favouritesByUser.put(userId, new ArrayList<>(favourites));
        }
    }

    /**
     * Test that adding a new favourite currency for a valid user succeeds,
     * and that the DAO is updated accordingly.
     */
    @Test
    void addNewFavourite_success() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "alice";
        // The user exists but initially has no favourites.
        gateway.setFavourites(userId, Collections.emptyList());

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                // Assert: the output data contains the expected user id and favourites.
                assertEquals(userId, outputData.getUserId());
                assertEquals(List.of("CAD"), outputData.getFavouriteCurrencies());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "CAD", true);

        // Act
        interactor.execute(input);

        // Assert: DAO state is in sync with the output data.
        assertEquals(List.of("CAD"), gateway.getFavouritesForUser(userId));
    }

    /**
     * Test that toggling an existing favourite currency off removes it
     * from the list of favourites for that user.
     */
    @Test
    void removeExistingFavourite_successToggleOff() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "alice";
        gateway.setFavourites(userId, List.of("CAD"));  // Already a favourite

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                assertEquals(userId, outputData.getUserId());
                assertTrue(outputData.getFavouriteCurrencies().isEmpty());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "CAD", true);

        // Act
        interactor.execute(input);

        // Assert: the favourite has been removed.
        assertTrue(gateway.getFavouritesForUser(userId).isEmpty());
    }

    /**
     * Test that an empty user id is treated as "user not logged in"
     * and that the interactor calls {@code prepareFailView}.
     */
    @Test
    void failure_userNotLoggedIn() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in FavouriteCurrencyInteractor.
                assertEquals("User is not logged in.", errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData("", "CAD", true);

        // Act
        interactor.execute(input);
    }

    /**
     * Test that a non-existing user id produces a failure view
     * with the expected error message.
     */
    @Test
    void failure_userDoesNotExist() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        // The gateway does not contain this user id, so userExists returns false.

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in FavouriteCurrencyInteractor.
                assertEquals("User does not exist.", errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData("ghost", "CAD", true);

        // Act
        interactor.execute(input);
    }

    /**
     * Test that an empty or blank currency code produces a failure view
     * with the expected error message.
     */
    @Test
    void failure_currencyCodeEmpty() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "alice";
        gateway.setFavourites(userId, Collections.emptyList());

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // NOTE: Adjust this string if you change it in FavouriteCurrencyInteractor.
                assertEquals("Currency code is empty.", errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "   ", true);

        // Act
        interactor.execute(input);
    }
}
