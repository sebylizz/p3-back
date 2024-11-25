package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    public void add(String first_name, String last_name, String email, Integer telephone, String address, Integer postalCode, String password, String verificationToken, Boolean verified) {
        Customer p = new Customer(first_name, last_name, email, telephone, address, postalCode, password, verificationToken, verified, null);
        persist(p);
    }

    public void addAdmin(String first_name, String last_name, String email, Integer telephone, String address, Integer postalCode, String password, String role) {
        Customer p = new Customer(first_name, last_name, email, telephone, address, postalCode, password,null, true, role);
        persist(p);
    }

    public void addResetToken(String email, String token, LocalDateTime expiration) {
        Customer customer = findByEmail(email);
        customer.setResetPasswordToken(token);
        customer.setResetPasswordTokenExpiration(expiration);
        persist(customer);
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Customer p = findById(id);
        delete(p);
    }

    public Customer findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Customer findByVerificationToken(String token) {
        return find("verificationToken", token).firstResult();
    }

    public Customer findByResetPasswordToken(String token) {
        return find("resetPasswordToken", token).firstResult();
    }

    public List<Customer> findBySearch(String searchString) {
        // Use a parameterized query to search in multiple fields
        return find("lower(firstName) like ?1 or lower(lastName) like ?2 or lower(email) like ?3",
                "%" + searchString.toLowerCase() + "%",
                "%" + searchString.toLowerCase() + "%",
                "%" + searchString.toLowerCase() + "%")
                .list();
    }
    
    
}
