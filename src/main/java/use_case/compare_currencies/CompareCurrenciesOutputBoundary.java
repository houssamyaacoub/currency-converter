package use_case.compare_currencies;

public interface CompareCurrenciesOutputBoundary {
    void present(CompareCurrenciesOutputData data);
    void prepareFailView(String errorMessage);
}
