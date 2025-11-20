package entity;

import java.time.Instant;

public class CurrencyConversion {
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private final double rate;
    private final Instant timestamp;

    // Updated the class to use timestamp as this is more useful in context of currency conversion
    public CurrencyConversion(Currency fromCurrency, Currency toCurrency, double rate, Instant timestamp) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.timestamp = timestamp ;
    }

    public double calculateConvertedAmount(double amount) {return amount * rate;}



    public Currency getFromCurrency() {return fromCurrency;}
    public Currency getToCurrency() {return toCurrency;}
    public double getRate() {return rate;}
    public Instant getTimeStamp() {return timestamp;}
}
