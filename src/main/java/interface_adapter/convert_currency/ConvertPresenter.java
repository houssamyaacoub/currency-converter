package interface_adapter.convert_currency;

import use_case.convert.ConvertOutputBoundary;
import use_case.convert.ConvertOutputData;

import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * Presenter for the Convert Currency Use Case.
 * This component acts as an Interface Adapter. It receives raw output data from
 * the Interactor (via the {@link ConvertOutputBoundary}), formats it for display
 * (e.g., formatting dates and currency amounts), and updates the {@link ConvertViewModel}.
 */
public class ConvertPresenter implements ConvertOutputBoundary {

    private final ConvertViewModel convertViewModel;

    /**
     * Constructs a new ConvertPresenter.
     *
     * @param convertViewModel The ViewModel to update with the results of the conversion.
     */
    public ConvertPresenter(ConvertViewModel convertViewModel) {
        this.convertViewModel = convertViewModel;
    }

    /**
     * Formats the successful conversion result and updates the View Model.
     *
     * @param outputData The raw output data containing the calculated amount, rate, and timestamp.
     */
    @Override
    public void present(ConvertOutputData outputData) {
        final ConvertState state = convertViewModel.getState();

        // Format amount to 2 decimal places for display
        final String convertedAmount = String.format(
                Locale.US, "%.2f", outputData.getConvertedAmount()
        );

        // Format timestamp into a readable date string
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .withZone(ZoneId.systemDefault());
        final String formattedTime = formatter.format(outputData.getTimestamp());

        // Create a detailed string for the rate info
        final String rateDetails = String.format(
                Locale.US,
                "Rate: %.4f | Date: %s",
                outputData.getRate(),
                formattedTime
        );

        // Update state with formatted strings and clear any previous errors
        state.setConvertedAmountResult(convertedAmount);
        state.setRateDetails(rateDetails);
        state.setError(null);

        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }

    /**
     * Formats a failure message and updates the View Model.
     *
     * @param errorMessage The error message explaining why the conversion failed.
     */
    @Override
    public void prepareFailView(String errorMessage) {
        final ConvertState state = convertViewModel.getState();

        state.setError(errorMessage);
        // Clear stale results so they don't linger behind the error message
        state.setConvertedAmountResult("0.00");
        state.setRateDetails("");

        convertViewModel.setState(state);
        convertViewModel.firePropertyChange();
    }
}
