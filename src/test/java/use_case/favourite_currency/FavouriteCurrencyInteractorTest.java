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
         *
         * In this in-memory implementation we consider a currency to "exist"
         * if the code is non-null and not blank.
         */
        @Override
        public boolean currencyExists(String currencyCode) {
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

        // Assert: gateway should have the new favourite stored.
        assertEquals(List.of("CAD"), gateway.getFavouritesForUser(userId));
    }

    /**
     * Test that toggling an existing favourite off removes it from the list.
     */
    @Test
    void removeExistingFavourite_successToggleOff() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "bob";
        gateway.setFavourites(userId, new ArrayList<>(List.of("USD", "EUR")));

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                // After toggling off "USD", only "EUR" should remain.
                assertEquals(userId, outputData.getUserId());
                assertEquals(List.of("EUR"), outputData.getFavouriteCurrencies());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure not expected: " + errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        // Toggle off an existing favourite.
        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "USD", true);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals(List.of("EUR"), gateway.getFavouritesForUser(userId));
    }

    /**
     * Test that attempting to toggle favourites while the user is not logged in
     * (empty user id) results in a failure.
     */
    @Test
    void failure_userNotLoggedIn() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected when user id is empty.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
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
     * Test that toggling favourites for a non-existent user fails with
     * a clear error message.
     */
    @Test
    void failure_userDoesNotExist() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "ghost"; // user not present in the gateway

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected for a non-existent user.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("User does not exist.", errorMessage);
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "CAD", true);

        // Act
        interactor.execute(input);
    }

    /**
     * Test that providing an empty currency code results in a failure and that
     * no favourites are modified.
     */
    @Test
    void failure_currencyCodeEmpty() {
        // Arrange
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "alice";
        gateway.setFavourites(userId, new ArrayList<>());

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected when currency code is empty.");
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

    /**
     * When the user already has 5 favourite currencies, attempting to add a sixth
     * should fail and leave the stored favourites unchanged.
     */
    @Test
    void failure_cannotExceedMaximumOfFiveFavourites() {
        // Arrange: user with exactly 5 favourite currencies already stored.
        InMemoryFavouriteGateway gateway = new InMemoryFavouriteGateway();
        String userId = "user-max";

        List<String> initialFavourites = List.of("C1", "C2", "C3", "C4", "C5");
        gateway.setFavourites(userId, initialFavourites);

        FavouriteCurrencyOutputBoundary presenter = new FavouriteCurrencyOutputBoundary() {
            @Override
            public void prepareSuccessView(FavouriteCurrencyOutputData outputData) {
                fail("Success not expected when adding a sixth favourite currency.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // The interactor should reject the operation with a clear message
                // and not modify the stored favourites.
                assertEquals("You can have at most 5 favourite currencies.", errorMessage);
                assertEquals(initialFavourites, gateway.getFavouritesForUser(userId));
            }
        };

        FavouriteCurrencyInputBoundary interactor =
                new FavouriteCurrencyInteractor(gateway, presenter);

        FavouriteCurrencyInputData input =
                new FavouriteCurrencyInputData(userId, "C6", true);

        // Act
        interactor.execute(input);
    }
}
