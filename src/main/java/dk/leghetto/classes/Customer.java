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

    public Customer() {
    }

    public Customer(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.newsletter = false;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = "user";
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
