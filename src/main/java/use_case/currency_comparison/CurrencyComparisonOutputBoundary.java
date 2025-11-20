package use_case.currency_comparison;

public interface CurrencyComparisonOutputBoundary {
    void prepareSuccessView(CurrencyComparisonOutputData outputData);
    void prepareFailView(String errorMessage);
}
