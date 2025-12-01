package data_access.offline_viewing;

import entity.OfflineRate;

public interface OfflineRateStrategy {
    OfflineRate loadRates() throws Exception;
}
