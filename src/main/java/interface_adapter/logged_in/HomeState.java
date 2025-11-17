package interface_adapter.logged_in;

/**
 * The State information representing the logged-in user.
 */
public class HomeState {
    private String fromCurrency = "CAD";
    private String toCurrency = "USD";
    private String amount = ""; // The user input
    private String conversionResult = null; // To show the answer
    private String error = null;
    private String username = "";
    private String password = "";
    private String passwordError;

    // Copy Constructor
    public HomeState(HomeState copy) {
        fromCurrency = copy.fromCurrency;
        toCurrency = copy.toCurrency;
        amount = copy.amount;
        conversionResult = copy.conversionResult;
        error = copy.error;
    }

    // Default Constructor
    public HomeState() {}

    // Getters and Setters
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getConversionResult() { return conversionResult; }
    public void setConversionResult(String conversionResult) { this.conversionResult = conversionResult; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }
}
















/*public class homeState {
    private String username = "";

    private String password = "";
    private String passwordError;

    public homeState(homeState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public homeState() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }
}*/
