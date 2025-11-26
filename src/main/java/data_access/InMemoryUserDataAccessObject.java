package data_access;

import entity.User;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;
import use_case.convert.ExchangeRateDataAccessInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


/**
 * In-memory implementation of the DAO for storing user data. This implementation does
 * NOT persist data between runs of the program.
 */
public class InMemoryUserDataAccessObject implements SignupUserDataAccessInterface,
                                                     LoginUserDataAccessInterface,
                                                     ChangePasswordUserDataAccessInterface,
                                                     LogoutUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();

    private String currentUsername;

    @Override
    public boolean existsByName(String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void changePassword(User user) {
        // Replace the old entry with the new password
        users.put(user.getName(), user);
    }

    // ====== Favourites / Recents for in-memory DAO (used mainly in tests) ======

    @Override
    public List<String> getFavouritesForUser(String username) {
        // Simple in-memory implementation: no favourites stored, return empty list.
        return new ArrayList<>();
    }

    @Override
    public void setFavouritesForUser(String username, List<String> favs) {
        // No-op for in-memory DAO. Can be extended later if needed.
    }

    @Override
    public Deque<String> getRecentsForUser(String username) {
        // Simple in-memory implementation: no recents stored, return empty deque.
        return new ArrayDeque<>();
    }

    @Override
    public void setRecentsForUser(String username, Deque<String> recents) {
        // No-op for in-memory DAO. Can be extended later if needed.
    }


}