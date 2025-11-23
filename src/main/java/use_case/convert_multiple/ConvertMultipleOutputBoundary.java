package use_case.convert_multiple;

public interface ConvertMultipleOutputBoundary {
    void present(ConvertMultipleOutputData outputData);
    void prepareFailView(String errorMessage);
}
