package use_case.offline_viewing;

import data_access.CurrencyListDAO;
import data_access.ExchangeRateHostDAO;
import use_case.convert.CurrencyRepository;

public class OfflineTest {
    public static void main(String[] args) {
        // 1. Build repositories / DAOs
        CurrencyRepository repo = new CurrencyListDAO();
        ExchangeRateHostDAO api = new ExchangeRateHostDAO(repo);

        // 2. Build strategies
        RateAccessStrategy online  = new OnlineRateStrategy(api, repo);
        RateAccessStrategy offline = new OfflineRateStrategy(repo);

        try {
            System.out.println("=== ONLINE STEP: fetch and cache ===");
            double onlineResult = online.convert("CAD", "USD", 100);
            System.out.println("Online result: 100 CAD = " + onlineResult + " USD");

            System.out.println("Now using OFFLINE (Cached)...");
            double offlineResult = offline.convert("CAD", "USD", 100);
            System.out.println("Offline result: 100 CAD = " + offlineResult + " USD");

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
