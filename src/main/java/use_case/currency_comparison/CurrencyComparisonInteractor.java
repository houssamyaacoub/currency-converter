package use_case.currency_comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CurrencyComparisonInteractor implements CurrencyComparisonInputBoundary {

    private static final int MAX_TARGETS = 5;

    private final CurrencyComparisonDataAccessInterface dataAccess;
    private final CurrencyComparisonOutputBoundary outputBoundary;

    public CurrencyComparisonInteractor(CurrencyComparisonDataAccessInterface dataAccess,
                                        CurrencyComparisonOutputBoundary outputBoundary) {
        this.dataAccess = dataAccess;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(CurrencyComparisonInputData inputData) {
        String base = inputData.getBaseCurrency();
        List<String> selectedTargets = new ArrayList<>(inputData.getTargetCurrencies());

        if (base == null || base.isBlank()) {
            outputBoundary.prepareFailView("Base currency must be selected.");
            return;
        }

        if (selectedTargets.isEmpty()) {
            outputBoundary.prepareFailView("At least one target currency must be selected.");
            return;
        }

        boolean limitedByMaxTargets = false;
        if (selectedTargets.size() > MAX_TARGETS) {
            selectedTargets = selectedTargets.subList(0, MAX_TARGETS);
            limitedByMaxTargets = true;
        }

        Map<String, Double> latestRates = dataAccess.getLatestRates(base, selectedTargets);

        List<CurrencyComparisonOutputData.CurrencyRate> availableRates = new ArrayList<>();
        List<String> unavailable = new ArrayList<>();

        for (String target : selectedTargets) {
            Double rate = latestRates.get(target);
            if (rate == null) {
                unavailable.add(target);
            } else {
                availableRates.add(new CurrencyComparisonOutputData.CurrencyRate(target, rate));
            }
        }

        if (availableRates.isEmpty()) {
            outputBoundary.prepareFailView("No currency rates were available for the selected currencies.");
            return;
        }

        availableRates.sort(
                Comparator.comparingDouble(CurrencyComparisonOutputData.CurrencyRate::getRate)
                        .reversed()
        );

        CurrencyComparisonOutputData outputData =
                new CurrencyComparisonOutputData(base, availableRates, unavailable, limitedByMaxTargets);

        outputBoundary.prepareSuccessView(outputData);
    }
}
