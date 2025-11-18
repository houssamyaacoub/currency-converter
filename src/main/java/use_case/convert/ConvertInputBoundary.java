package use_case.convert;


/**
 * The Input Boundary Interface (Port) for the Convert Currency Use Case.
 * * This interface is implemented by the Interactor and is called by the Controller.
 * It is the entry point for the business logic.
 */
public interface ConvertInputBoundary {

    /**
     * Executes the currency conversion use case.
     * * @param inputData The {@link ConvertInputData} containing the amount,
     * base currency, and target currency provided by the user.
     */
    void execute(ConvertInputData inputData);
}
