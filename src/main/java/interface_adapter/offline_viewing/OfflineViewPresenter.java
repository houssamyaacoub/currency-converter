package interface_adapter.offline_viewing;

import use_case.offline_viewing.OfflineViewOutputBoundary;
import use_case.offline_viewing.OfflineViewOutputData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Presenter for offline viewing.
 * Translates use-case output into OfflineViewModel fields.
 */
public class OfflineViewPresenter implements OfflineViewOutputBoundary {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private final OfflineViewModel viewModel;

    public OfflineViewPresenter(OfflineViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(OfflineViewOutputData outputData) {
        String msg = "Offline Data - Last Updated: " +
                FORMATTER.format(outputData.getTimestamp());

        viewModel.setRates(outputData.getRates());
        viewModel.setTimestamp(outputData.getTimestamp());
        viewModel.setStatusMessage(msg);
    }

    @Override
    public void prepareFailView(String error) {
        String msg = (error == null || error.isEmpty())
                ? "Offline data unavailable."
                : error;

        viewModel.setRates(Collections.emptyMap());
        viewModel.setTimestamp(null);
        viewModel.setStatusMessage(msg);
    }
}
