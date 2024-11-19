package dk.leghetto.classes;

public class ResetPasswordRequest {
    private String token;
    private String password;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getPassword() { return password; }

    public String getToken() { return token; }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
