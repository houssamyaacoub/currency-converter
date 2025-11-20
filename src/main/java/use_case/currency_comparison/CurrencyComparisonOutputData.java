package use_case.currency_comparison;

import java.util.List;

public class CurrencyComparisonOutputData {

    private final String baseCurrency;
    private final List<CurrencyRate> rates;
    private final List<String> unavailableCurrencies;
    private final boolean limitedByMaxTargets;

    public CurrencyComparisonOutputData(String baseCurrency,
                                        List<CurrencyRate> rates,
                                        List<String> unavailableCurrencies,
                                        boolean limitedByMaxTargets) {
        this.baseCurrency = baseCurrency;
        this.rates = rates;
        this.unavailableCurrencies = unavailableCurrencies;
        this.limitedByMaxTargets = limitedByMaxTargets;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public List<String> getUnavailableCurrencies() {
        return unavailableCurrencies;
    }

    public boolean isLimitedByMaxTargets() {
        return limitedByMaxTargets;
    }

    public static class CurrencyRate {
        private final String currencyCode;
        private final double rate;

        public CurrencyRate(String currencyCode, double rate) {
            this.currencyCode = currencyCode;
            this.rate = rate;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public double getRate() {
            return rate;
        }
    }
}
