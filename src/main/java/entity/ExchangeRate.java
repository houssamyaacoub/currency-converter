package entity;

import java.time.LocalDate;

public class ExchangeRate {
    private final Currency fromCurrency;
    private final Currency toCurrency;
    private final double rate;
    private final LocalDate date;

    public ExchangeRate(Currency fromCurrency, Currency toCurrency, double rate, LocalDate date) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.date = date;
    }

    public double convert(double amount) {return amount * rate;}

    public Currency getFromCurrency() {return fromCurrency;}
    public Currency getToCurrency() {return toCurrency;}
    public double getRate() {return rate;}
    public LocalDate getDate() {return date;}
}
