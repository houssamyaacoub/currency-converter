package interface_adapter.logged_in;

/**
 * The State information representing the logged-in user.
 */
public class HomeState {
    private String username = "";
    private String password = "";
    private String passwordError;

    // Copy Constructor
    public HomeState(HomeState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;
    }

    // Default Constructor
    public HomeState() {}

    // Getters and Setters

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
}




