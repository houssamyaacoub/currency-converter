package use_case.offline_viewing;

import data_access.offline_viewing.PairRateCache;

import java.time.Instant;
import java.util.Map;

public class OfflineViewInteractor implements OfflineViewInputBoundary {

    private final PairRateCache cache;
    private final OfflineViewOutputBoundary presenter;

    public OfflineViewInteractor(PairRateCache cache,
                                 OfflineViewOutputBoundary presenter) {
        this.cache = cache;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        try {
            Map<String, Double> rates = cache.getAllRates();
            Instant latestTs = cache.getLatestTimestamp();

            if (rates == null || rates.isEmpty() || latestTs == null) {
                presenter.prepareFailView("Offline data unavailable.");
                return;
            }

            // Note: latestTs is *the most recent* time any pair was updated
            OfflineViewOutputData output =
                    new OfflineViewOutputData(rates, latestTs);
            presenter.present(output);

        } catch (Exception e) {
            presenter.prepareFailView("Offline data unavailable.");
        }
    }
}
