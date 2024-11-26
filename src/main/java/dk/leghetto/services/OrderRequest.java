package dk.leghetto.services;

public class OrderRequest {

    private String firstName;
    private String lastName;
    private String address;
    private Integer postalCode;
    private Integer phoneNumber;
    private String email;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public Integer getPostalCode() { return postalCode; }
    public Integer getPhoneNumber() { return phoneNumber; }
    public String getEmail() {
        return email;
    }

}
