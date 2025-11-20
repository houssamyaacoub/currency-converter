package use_case.convert;

public interface ConvertOutputBoundary {
    void prepareSuccessView(ConvertOutputData outputData);
    void prepareFailView(String errorMessage);
}