package interface_adapter.convert_currency;

import use_case.convert.ConvertOutputBoundary;
import use_case.convert.ConvertOutputData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConvertPresenter implements ConvertOutputBoundary {

    private final ConvertViewModel convertViewModel;

    public ConvertPresenter(ConvertViewModel convertViewModel) {
        this.convertViewModel = convertViewModel;
    }

    @Override
    public void present(ConvertOutputData outputData) {
        ConvertState state = convertViewModel.getState();

        // Format amount to 2 decimals
        String convertedAmount = String.format(
                Locale.US, "%.2f", outputData.getConvertedAmount()
        );

        // Format timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withZone(ZoneId.systemDefault());
        String formattedTime = formatter.format(outputData.getTimestamp());

        String rateDetails = String.format(
                Locale.US,
                "Rate: %.4f | Date: %s",
                outputData.getRate(),
                formattedTime
        );

        state.setConvertedAmountResult(convertedAmount);
        state.setRateDetails(rateDetails);
        state.setError(null);  // clear any previous error

        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ConvertState state = convertViewModel.getState();

        state.setError(errorMessage);
        // Clear stale results so they don't linger behind the error
        state.setConvertedAmountResult("0.00");
        state.setRateDetails("");

        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }
}
