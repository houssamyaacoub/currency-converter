package use_case.offline_viewing;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

public interface RateAccessStrategy {

    double convert(String from, String to, double amount) throws ServiceException;

    Map<String, Double> getLatestRates(String base) throws ServiceException;

    SortedMap<LocalDate, Double> getHistoricalRates(
            String from, String to, LocalDate start, LocalDate end) throws ServiceException;

    String getModeName();

}
