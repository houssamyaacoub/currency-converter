package interface_adapter.convert_currency;

import use_case.convert_multiple.ConvertMultipleInputBoundary;
import use_case.convert_multiple.ConvertMultipleInputData;

import java.util.List;

public class ConvertMultipleController {

    private final ConvertMultipleInputBoundary useCase;

    public ConvertMultipleController(ConvertMultipleInputBoundary useCase) {
        this.useCase = useCase;
    }

    public void execute(String amountStr, String baseCurrencyName, List<String> targetCurrencyNames) {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            amount = 0.0;
        }
        ConvertMultipleInputData inputData =
                new ConvertMultipleInputData(amount, baseCurrencyName, targetCurrencyNames);
        useCase.execute(inputData);
    }
}
