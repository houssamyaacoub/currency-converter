package use_case.offline_viewing;

public interface OfflineViewOutputBoundary {
    void present(OfflineViewOutputData outputData);
    void prepareFailView(String error);
}
