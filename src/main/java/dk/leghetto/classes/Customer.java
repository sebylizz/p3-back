package dk.leghetto.classes;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.Username;
import io.quarkus.security.jpa.UserDefinition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;

@Entity
@UserDefinition
@Table(name = "users")
public class Customer extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "telephone")
    private Integer telephone;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code")
    private Integer postalCode;

    @Username
    @Column(name = "email")
    private String email;

    @Column(name = "newsletter")
    private Boolean newsletter;

    @Password
    @Column(name = "password")
    private String password;

    @Roles
    @Column(name = "role")
    private String role;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiration")
    private LocalDateTime resetPasswordTokenExpiration;

    public Customer() {
    }

    public Customer(String firstName, String lastName, String email, Integer telephone, String address, Integer postalCode, String password, String verificationToken, Boolean verified) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.address = address;
        this.postalCode = postalCode;
        this.newsletter = false;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = "user";
        this.verificationToken = verificationToken;
        this.verified = verified;
    }

    public boolean matchPsw(String password) {
        return BcryptUtil.matches(password, this.password);
    }

    public Long getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return password;
    }

    public Boolean getNewsletter() {
        return newsletter;
    }

    public String getRole() {
        return role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = BcryptUtil.bcryptHash(password);
    }

    public void setVerified(Boolean verified) { //for at ændre verification status efter verification link er trykket
        this.verified = verified;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public void setResetPasswordTokenExpiration(LocalDateTime resetPasswordTokenExpiration) {
        this.resetPasswordTokenExpiration = resetPasswordTokenExpiration;
    }
    
    @Transactional
    public static Customer updateCustomer(Long id, String firstName, String lastName, String email) {
        Customer customer = findById(id);
        if (customer == null) {
            throw new NotFoundException("Customer not found");
        }

        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);

        customer.persist(); // Automatically updates or persists the entity
        return customer;
    }
      
    public String verificationToken() { return verificationToken; }

    public Boolean verified() { return verified; }

    public LocalDateTime getResetPasswordTokenExpiration() { return resetPasswordTokenExpiration; }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", telephone=" + telephone +
                ", email='" + email + '\'' +
                ", newsletter=" + newsletter +
                ", role='" + role + '\'' +
                '}';
    }
}
