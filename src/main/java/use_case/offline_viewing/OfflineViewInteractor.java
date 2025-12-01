package use_case.offline_viewing;

import entity.OfflineRate;

public class OfflineViewInteractor implements OfflineViewInputBoundary {

    private final OfflineRateStrategy strategy;
    private final OfflineViewOutputBoundary presenter;

    public OfflineViewInteractor(OfflineRateStrategy strategy,
                                 OfflineViewOutputBoundary presenter) {
        this.strategy = strategy;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        try {
            OfflineRate result = strategy.loadRates();

            presenter.present(
                    new OfflineViewOutputData(
                            result.getRates(),
                            result.getTimestamp()
                    )
            );

        } catch (Exception e) {
            presenter.prepareFailView("Offline data unavailable.");
        }
    }
}
