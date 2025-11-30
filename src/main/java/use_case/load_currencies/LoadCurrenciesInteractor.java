package use_case.load_currencies;

import entity.Currency;
import use_case.convert.CurrencyRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor responsible for loading the list of currencies from the Repository
 * and pushing the results to the Presenter.
 */
public class LoadCurrenciesInteractor implements LoadCurrenciesInputBoundary {

    private final CurrencyRepository currencyRepository;
    private final LoadCurrenciesOutputBoundary presenter;

    public LoadCurrenciesInteractor(CurrencyRepository currencyRepository, LoadCurrenciesOutputBoundary presenter) {
        this.currencyRepository = currencyRepository;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        try {

            Iterator<Currency> currencyIterator = currencyRepository.getCurrencyIterator();

            if (!currencyIterator.hasNext()) {
                throw new RuntimeException("Currency list is empty. Check API key and file existence.");
            }

            // 2. Transform the Entities into the required List of Names (for the ComboBox)
            List<String> currencyNames = new ArrayList<>();
            while (currencyIterator.hasNext()) {
                currencyNames.add(currencyIterator.next().getName());
            }

            // 3. Prepare and Send Output
            LoadCurrenciesOutputData outputData = new LoadCurrenciesOutputData(currencyNames);
            presenter.presentSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Failed to load currency list: " + e.getMessage());
        }
    }
}