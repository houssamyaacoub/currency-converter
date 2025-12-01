package interface_adapter.offline_viewing;

import use_case.offline_viewing.OfflineViewOutputBoundary;
import use_case.offline_viewing.OfflineViewOutputData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OfflineViewPresenter implements OfflineViewOutputBoundary {

    private final OfflineViewModel offlineViewModel;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault());

    public OfflineViewPresenter(OfflineViewModel offlineViewModel) {
        this.offlineViewModel = offlineViewModel;
    }

    @Override
    public void present(OfflineViewOutputData outputData) {
        offlineViewModel.setRates(outputData.getRates());
        offlineViewModel.setTimestamp(outputData.getTimestamp());

        String formatted = FORMATTER.format(outputData.getTimestamp());
        offlineViewModel.setStatusMessage(
                "Offline Data – Last Updated: " + formatted
        );
    }

    @Override
    public void prepareFailView(String error) {
        offlineViewModel.setStatusMessage(error);
    }
}
