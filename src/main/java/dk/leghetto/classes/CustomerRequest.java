package dk.leghetto.classes;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class CustomerRequest {
    @Schema(description = "First name", required = true)
    private String firstName;

    @Schema(description = "Last name", required = true)
    private String lastName;

    @Schema(description = "Email address", required = true)
    private String email;

    @Schema(description = "Password", required = true)
    private String password;

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

