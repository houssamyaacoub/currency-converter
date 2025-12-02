package use_case.recent_currency;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interactor for the Recent / Frequently Used Currencies use case (Use Case 8).
 * This class implements the application business rules for:
 *  - recording currency usage after a successful conversion, and
 *  - computing an ordered list of currencies:
 *        favourites -> top frequent -> all others.
 */
public class RecentCurrencyInteractor implements RecentCurrencyInputBoundary {

    /** Maximum number of top frequent currencies to expose (e.g., 5). */
    private static final int MAX_TOP_FREQUENT = 5;

    private final RecentCurrencyDataAccessInterface recentGateway;
    private final RecentCurrencyOutputBoundary presenter;

    /**
     * Constructs a new RecentCurrencyInteractor.
     *
     * @param recentGateway the gateway used to access and store usage counts.
     * @param presenter     the output boundary used to present results.
     */
    public RecentCurrencyInteractor(RecentCurrencyDataAccessInterface recentGateway,
                                    RecentCurrencyOutputBoundary presenter) {
        this.recentGateway = recentGateway;
        this.presenter = presenter;
    }

    /**
     * Records the usage of the currencies contained in {@code inputData}
     * for the given user and computes a new ordered currency list.
     * Behaviour:
     * <ul>
     *   <li>Validates that the user exists.</li>
     *   <li>Records usage for both the "from" and "to" currencies (if non-empty).</li>
     *   <li>Combines favourites, most frequently used currencies, and all supported
     *       currencies into a single ordered list with duplicates removed.</li>
     *   <li>Notifies the presenter with a {@link RecentCurrencyOutputData} object.</li>
     * </ul>
     *
     * @param inputData information about which user and which currencies
     *                  were involved in the conversion.
     */

    @Override
    public void execute(RecentCurrencyInputData inputData) {
        String userId = inputData.getUserId();
        if (userId == null || userId.isEmpty()) {
            presenter.prepareFailView("User is not logged in.");
            return;
        }
        if (!recentGateway.userExists(userId)) {
            presenter.prepareFailView("User does not exist.");
            return;
        }

        String from = normalize(inputData.getFromCurrencyCode());
        String to = normalize(inputData.getToCurrencyCode());
        // 1. Record usage for each valid currency.
        if (from != null) {
            recentGateway.recordUsage(userId, from);
        }
        if (to != null) {
            recentGateway.recordUsage(userId, to);
        }

        // 2. Load current usage counts, favourites, and all supported currencies.
        Map<String, Integer> usageCounts = recentGateway.getUsageCounts(userId);
        List<String> favourites = recentGateway.getFavouriteCurrencies(userId);
        List<String> allSupported = recentGateway.getAllSupportedCurrencies();

        // 3. Compute top frequent currencies (by usage count, descending).
        List<String> topFrequent = computeTopFrequent(usageCounts, MAX_TOP_FREQUENT);

        // 4. Build final ordered list: favourites -> top frequent -> all others.
        List<String> ordered = buildOrderedList(favourites, topFrequent, allSupported);

        RecentCurrencyOutputData outputData =
                new RecentCurrencyOutputData(userId, favourites, topFrequent, ordered);

        presenter.prepareSuccessView(outputData);
    }

    /**
     * Normalizes a currency code by trimming and converting to upper case.
     *
     * @param code the raw currency code.
     * @return the normalized code, or null if empty or null.
     */
    private String normalize(String code) {
        if (code == null) {
            return null;
        }
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    /**
     * Computes the top N frequent currencies based on usage counts.
     * Uses Collectors.toList() to ensure compatibility with Java &lt; 16.
     *
     * @param usageCounts a map from currency code to its usage count for the user;
     *                    may be {@code null} or empty.
     * @param maxCount    the maximum number of currencies to return.
     * @return a list of up to {@code maxCount} currency codes ordered from
     *         most frequently used to least frequently used.
     */
    private List<String> computeTopFrequent(Map<String, Integer> usageCounts, int maxCount) {
        if (usageCounts == null || usageCounts.isEmpty()) {
            return new ArrayList<>();
        }

        return usageCounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());  // <-- FIX for Java 16+
    }

    /**
     * Builds an ordered list of currency codes using the rules:
     *  1. favourites first,
     *  2. top frequent currencies next,
     *  3. all remaining supported currencies last.
     *
     * Duplicates are removed while preserving insertion order.
     *
     * @param favourites   the list of favourite currency codes for the user; may be {@code null}.
     * @param topFrequent  the list of top frequent currency codes; may be {@code null}.
     * @param allSupported the list of all supported currency codes; may be {@code null}.
     * @return a new list containing the ordered combination of all provided codes
     *         with duplicates removed.
     */
    private List<String> buildOrderedList(List<String> favourites,
                                          List<String> topFrequent,
                                          List<String> allSupported) {

        LinkedHashSet<String> ordered = new LinkedHashSet<>();

        if (favourites != null) {
            for (String fav : favourites) {
                String normalized = normalize(fav);
                if (normalized != null) {
                    ordered.add(normalized);
                }
            }
        }

        if (topFrequent != null) {
            for (String freq : topFrequent) {
                String normalized = normalize(freq);
                if (normalized != null) {
                    ordered.add(normalized);
                }
            }
        }

        if (allSupported != null) {
            for (String code : allSupported) {
                String normalized = normalize(code);
                if (normalized != null) {
                    ordered.add(normalized);
                }
            }
        }

        return new ArrayList<>(ordered);
    }
}
