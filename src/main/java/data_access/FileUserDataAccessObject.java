package data_access;

import entity.User;
import entity.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Collections;
import use_case.favourite_currency.FavouriteCurrencyDataAccessInterface;
import use_case.recent_currency.RecentCurrencyDataAccessInterface;
import data_access.CurrencyListDAO;
import java.util.LinkedHashSet;

/**
 * DAO for user data implemented using a File to persist the data.
 */
public class FileUserDataAccessObject implements SignupUserDataAccessInterface,
                                                 LoginUserDataAccessInterface,
                                                 ChangePasswordUserDataAccessInterface,
                                                 LogoutUserDataAccessInterface,
                                                 FavouriteCurrencyDataAccessInterface,
                                                 RecentCurrencyDataAccessInterface{

    private static final String HEADER = "username,password,favourites,recents";

    private final File csvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>();
    private final Map<String, User> accounts = new HashMap<>();
    private final Map<String, java.util.List<String>> favouritesByUser = new HashMap<>();
    private final Map<String, java.util.Deque<String>> recentsByUser = new HashMap<>();

    private String currentUsername;
    /** Used to provide the list of all supported currencies. */
    private final CurrencyListDAO currencyListDAO;


    /**
     * Construct this DAO for saving to and reading from a local file.
     * @param csvPath the path of the file to save to
     * @param userFactory factory for creating user objects
     * @throws RuntimeException if there is an IOException when accessing the file
     */
    public FileUserDataAccessObject(String csvPath, UserFactory userFactory,CurrencyListDAO currencyListDAO) {
        this.currencyListDAO = currencyListDAO;


        csvFile = new File(csvPath);
        headers.put("username", 0);
        headers.put("password", 1);
        headers.put("favourites", 2);
        headers.put("recents", 3);

        if (csvFile.length() == 0) {
            save();
        }
        else {

            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                final String header = reader.readLine();

                // Accept both the new header and the legacy header "username,password".
                if (!HEADER.equals(header) && !"username,password".equals(header)) {
                    throw new RuntimeException(String.format("header should be%n: %s%n but was:%n%s", HEADER, header));
                }

                String row;
                while ((row = reader.readLine()) != null) {
                    final String[] col = row.split(",", -1);
                    final String username = String.valueOf(col[headers.get("username")]);
                    final String password = String.valueOf(col[headers.get("password")]);
                    final User user = userFactory.create(username, password);
                    accounts.put(username, user);
                    // Favourites (column may not exist in old file)
                    java.util.List<String> favs = new ArrayList<>();
                    if (col.length > headers.get("favourites")) {
                        String favStr = col[headers.get("favourites")];
                        if (favStr != null && !favStr.isEmpty()) {
                            for (String token : favStr.split(";")) {
                                String trimmed = token.trim();
                                if (!trimmed.isEmpty()) {
                                    favs.add(trimmed);
                                }
                            }
                        }
                    }
                    favouritesByUser.put(username, favs);

                    // Recents
                    java.util.Deque<String> recents = new java.util.ArrayDeque<>();
                    if (col.length > headers.get("recents")) {
                        String recentStr = col[headers.get("recents")];
                        if (recentStr != null && !recentStr.isEmpty()) {
                            for (String token : recentStr.split(";")) {
                                String trimmed = token.trim();
                                if (!trimmed.isEmpty()) {
                                    recents.addLast(trimmed); // oldest first or latest first,看你约定
                                }
                            }
                        }
                    }
                    recentsByUser.put(username, recents);

                }
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    /**
     * Saves all users and their associated favourites/recents to the CSV file.
     * Format:
     *   username,password,favourites,recents
     * where favourites and recents are ";"-separated lists of currency codes.
     */
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // Write header
            writer.write(HEADER);
            writer.newLine();

            // Write each user as one row
            for (User user : accounts.values()) {
                String username = user.getName();
                String password = user.getPassword();

                // Favourites for this user (semicolon-separated)
                List<String> favs = favouritesByUser.get(username);
                if (favs == null) {
                    favs = Collections.emptyList();
                }
                String favStr = String.join(";", favs);

                // Recents for this user (semicolon-separated, most recent first)
                Deque<String> recents = recentsByUser.get(username);
                String recentStr = "";
                if (recents != null && !recents.isEmpty()) {
                    recentStr = String.join(";", recents);
                }

                // username,password,favourites,recents
                String line = username + "," + password + "," + favStr + "," + recentStr;
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void save(User user) {
        accounts.put(user.getName(), user);
        this.save();
    }

    @Override
    public User get(String username) {
        return accounts.get(username);
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
    public boolean existsByName(String identifier) {
        return accounts.containsKey(identifier);
    }

    @Override
    public void changePassword(User user) {
        // Replace the User object in the map
        accounts.put(user.getName(), user);
        save();
    }

    @Override
    public boolean userExists(String userId) {
        return accounts.containsKey(userId);
    }

    @Override
    public boolean currencyExists(String currencyCode) {
        // Simple implementation — can be improved with CurrencyListDAO later
        return currencyCode != null && !currencyCode.trim().isEmpty();
    }



    /**
     * Returns a copy of the favourites list for a given user.
     */
    public List<String> getFavouritesForUser(String username) {
        List<String> favs = favouritesByUser.get(username);
        if (favs == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(favs); // return a copy so caller cannot mutate internal state
    }

    /**
     * Replaces the favourites list for a given user.
     */
    public void setFavouritesForUser(String username, List<String> favs) {
        favouritesByUser.put(username, new ArrayList<>(favs)); // store a copy
        save(); // persist to users.csv
    }


    @Override
    public void saveFavouritesForUser(String userId, List<String> favourites) {
        setFavouritesForUser(userId, favourites);
    }

    /**
     * Returns a copy of the recents deque for a given user.
     * Most recent should be first.
     */
    public Deque<String> getRecentsForUser(String username) {
        Deque<String> rec = recentsByUser.get(username);
        if (rec == null) {
            return new ArrayDeque<>();
        }
        return new ArrayDeque<>(rec); // return a copy
    }

    /**
     * Replace the recents deque for a given user.
     */
    public void setRecentsForUser(String username, Deque<String> recents) {
        recentsByUser.put(username, new ArrayDeque<>(recents)); // store a copy
        save(); // persist to users.csv
    }

    // =====================================================================================
    //  RecentCurrencyDataAccessInterface implementation
    // =====================================================================================

    /**
     * Record that a currency was used by a given user.
     * Behaviour:
     *  - Only keep the last 5 distinct currencies (most recent first).
     *  - If the currency is in the user's favourites, do NOT store it in recents.
     */
    @Override
    public void recordUsage(String userId, String currencyCode) {
        if (userId == null || !accounts.containsKey(userId)) {
            return;
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return;
        }

        String code = currencyCode.trim();

        // Do not keep favourites in recent.
        List<String> favourites = favouritesByUser.getOrDefault(userId, new ArrayList<>());
        if (favourites.contains(code)) {
            // Remove from recents if present.
            Deque<String> existing = recentsByUser.get(userId);
            if (existing != null) {
                existing.remove(code);
            }
            save();
            return;
        }

        // Get or create deque for this user.
        Deque<String> recents = recentsByUser.computeIfAbsent(userId, k -> new ArrayDeque<>());

        // Move this code to the front (most recent).
        recents.remove(code);
        recents.addFirst(code);

        // Trim to max 5.
        while (recents.size() > 5) {
            recents.removeLast();
        }

        save();
    }

    /**
     * Returns a pseudo "usage count" map derived from recency:
     * the more recent a currency is, the higher the assigned score.
     */
    @Override
    public Map<String, Integer> getUsageCounts(String userId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        Deque<String> recents = recentsByUser.get(userId);
        if (recents == null) {
            return result;
        }

        int score = recents.size();
        for (String code : recents) {
            result.put(code, score--);
        }
        return result;
    }

    /**
     * Returns the list of favourite currencies for this user, as required by
     * RecentCurrencyDataAccessInterface.
     */
    @Override
    public List<String> getFavouriteCurrencies(String userId) {
        return getFavouritesForUser(userId);
    }

    /**
     * Returns all supported currencies as display strings (e.g., "CAD", "USD", ...).
     * This uses CurrencyListDAO as the single source of truth.
     */
    @Override
    public List<String> getAllSupportedCurrencies() {
        return currencyListDAO.getAllCurrencies()
                .stream()
                .map(c -> c.getName())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Builds the ordered currency list for a given user:
     *   1. Favourites (in the order stored in favouritesByUser)
     *   2. Up to 5 recent currencies (most recent first, excluding favourites)
     *   3. All remaining supported currencies
     */
    @Override
    public List<String> getOrderedCurrenciesForUser(String userId) {
        List<String> all = getAllSupportedCurrencies();
        List<String> favourites = getFavouritesForUser(userId);
        Deque<String> recents = recentsByUser.getOrDefault(userId, new ArrayDeque<>());

        LinkedHashSet<String> ordered = new LinkedHashSet<>();

        // 1. Add favourites first (if they are valid currencies).
        for (String fav : favourites) {
            if (fav == null) {
                continue;
            }
            String trimmed = fav.trim();
            if (!trimmed.isEmpty() && all.contains(trimmed)) {
                ordered.add(trimmed);
            }
        }

        // 2. Then recents (excluding favourites).
        for (String r : recents) {
            if (r == null) {
                continue;
            }
            String trimmed = r.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (all.contains(trimmed) && !favourites.contains(trimmed)) {
                ordered.add(trimmed);
            }
        }

        // 3. Finally, add any remaining currencies.
        for (String cur : all) {
            if (cur != null && !cur.trim().isEmpty()) {
                ordered.add(cur);
            }
        }

        return new ArrayList<>(ordered);
    }



}
