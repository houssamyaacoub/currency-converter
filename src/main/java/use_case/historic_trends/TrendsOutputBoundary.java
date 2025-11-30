
package use_case.historic_trends;

public interface TrendsOutputBoundary {
    void prepareSuccessView(TrendsOutputData data);
    void prepareFailView(String errorMessage);
    void prepareHomeView();
}
