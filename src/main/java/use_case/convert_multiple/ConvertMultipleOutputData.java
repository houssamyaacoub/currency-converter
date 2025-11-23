package use_case.convert_multiple;

import java.time.Instant;
import java.util.List;

public class ConvertMultipleOutputData {

    private final double amount;
    private final String baseCurrencyName;
    private final boolean limitedByMaxTargets;
    private final List<ConversionResult> conversions;
    private final List<String> failedTargets;

    public ConvertMultipleOutputData(double amount,
                                     String baseCurrencyName,
                                     boolean limitedByMaxTargets,
                                     List<ConversionResult> conversions,
                                     List<String> failedTargets) {
        this.amount = amount;
        this.baseCurrencyName = baseCurrencyName;
        this.limitedByMaxTargets = limitedByMaxTargets;
        this.conversions = conversions;
        this.failedTargets = failedTargets;
    }

    public double getAmount() {
        return amount;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public boolean isLimitedByMaxTargets() {
        return limitedByMaxTargets;
    }

    public List<ConversionResult> getConversions() {
        return conversions;
    }

    public List<String> getFailedTargets() {
        return failedTargets;
    }

    public static class ConversionResult {
        private final String targetCurrencyName;
        private final String targetCurrencySymbol;
        private final double rate;
        private final double convertedAmount;
        private final Instant timestamp;

        public ConversionResult(String targetCurrencyName,
                                String targetCurrencySymbol,
                                double rate,
                                double convertedAmount,
                                Instant timestamp) {
            this.targetCurrencyName = targetCurrencyName;
            this.targetCurrencySymbol = targetCurrencySymbol;
            this.rate = rate;
            this.convertedAmount = convertedAmount;
            this.timestamp = timestamp;
        }

        public String getTargetCurrencyName() {
            return targetCurrencyName;
        }

        public String getTargetCurrencySymbol() {
            return targetCurrencySymbol;
        }

        public double getRate() {
            return rate;
        }

        public double getConvertedAmount() {
            return convertedAmount;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
