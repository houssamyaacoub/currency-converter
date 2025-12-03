package use_case.load_currencies;

import entity.Currency;
import use_case.convert.CurrencyRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Interactor for the Load Currencies Use Case.
 * This class implements the business logic for retrieving the list of supported currencies.
 * It acts as the coordinator between the Data Access Layer (via {@link CurrencyRepository})
 * and the Presentation Layer (via {@link LoadCurrenciesOutputBoundary}).
 */
public class LoadCurrenciesInteractor implements LoadCurrenciesInputBoundary {

    private final CurrencyRepository currencyRepository;
    private final LoadCurrenciesOutputBoundary presenter;

    /**
     * Constructs a new LoadCurrenciesInteractor.
     *
     * @param currencyRepository The gateway to the currency data source (file/API).
     * @param presenter          The presenter to receive the loaded data or error messages.
     */
    public LoadCurrenciesInteractor(CurrencyRepository currencyRepository, LoadCurrenciesOutputBoundary presenter) {
        this.currencyRepository = currencyRepository;
        this.presenter = presenter;
    }

    /**
     * Executes the use case to load all available currencies.
     * It retrieves an iterator from the repository, extracts the currency names,
     * and passes the list to the presenter. If the list is empty or an error occurs,
     * it notifies the presenter of the failure.
     */
    @Override
    public void execute() {
        try {
            // 1. Fetch the Iterator from the Repository
            // This abstraction allows traversing the data without exposing the underlying structure (List/Map).
            Iterator<Currency> currencyIterator = currencyRepository.getCurrencyIterator();

            // Check if the iterator has any elements before processing
            if (!currencyIterator.hasNext()) {
                throw new RuntimeException("Currency list is empty. Check API key and file existence.");
            }

            // 2. Transform the Entities into a List of Names (Data Processing)
            // We extract only the names required for the UI dropdowns.
            List<String> currencyNames = new ArrayList<>();
            while (currencyIterator.hasNext()) {
                currencyNames.add(currencyIterator.next().getName());
            }

            // 3. Prepare Output Data and Pass to Presenter
            LoadCurrenciesOutputData outputData = new LoadCurrenciesOutputData(currencyNames);
            presenter.presentSuccessView(outputData);

        } catch (Exception e) {
            // Handle any errors during data retrieval or processing
            presenter.prepareFailView("Failed to load currency list: " + e.getMessage());
        }
    }
}