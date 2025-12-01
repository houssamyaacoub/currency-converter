package use_case.offline_viewing;

/**
 * Holds the current strategy (online or offline) and lets you switch.
 */
public class RateContext {

    private RateAccessStrategy strategy;

    public RateContext(RateAccessStrategy initial) {
        this.strategy = initial;
    }

    public void setStrategy(RateAccessStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    public RateAccessStrategy getStrategy() {
        System.out.println("✔ Current strategy: " + strategy.getModeName());
        return strategy;

    }

}

