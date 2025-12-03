package use_case.convert;

/**
 * The Output Boundary Interface (Port) for the Convert Currency Use Case.
 * This interface is implemented by the Presenter and is called by the Interactor
 * to deliver the final result (success or failure) of the use case.
 */
public interface ConvertOutputBoundary {

    /**
     * Presents the result of the currency conversion use case (Success View).
     *
     * @param outputData The {@link ConvertOutputData} containing the successful
     * conversion details.
     */
    void present(ConvertOutputData outputData);

    /**
     * Prepares the failure view for the currency conversion use case.
     *
     * @param errorMessage The explanation of the failure (e.g., "Invalid currency code").
     */
    void prepareFailView(String errorMessage);
}