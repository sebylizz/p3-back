package dk.leghetto.services;

public class CustomerRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Integer telephone;
    private String address;
    private Integer postalCode;
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getTelephone() {
        return telephone;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
