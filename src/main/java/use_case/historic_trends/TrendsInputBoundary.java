
package use_case.historic_trends;

public interface TrendsInputBoundary {

    void execute(TrendsInputData trendsInputData);
    void switchToHomeView();
}
