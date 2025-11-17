package use_case.convert;

import use_case.login.LoginInputData;

public interface ConvertInputBoundary {

    void execute(ConvertInputData convertInputData);
}
