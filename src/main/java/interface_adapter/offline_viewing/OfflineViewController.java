package interface_adapter.offline_viewing;

import use_case.offline_viewing.OfflineViewInputBoundary;

public class OfflineViewController {

    private final OfflineViewInputBoundary interactor;

    public OfflineViewController(OfflineViewInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void loadOfflineRates() {
        interactor.execute();
    }
}
