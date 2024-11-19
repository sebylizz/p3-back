package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    public void add(String first_name, String last_name, String email, String password, String verificationToken, Boolean verified) {
        Customer p = new Customer(first_name, last_name, email, password, verificationToken, verified);
        persist(p);
    }

    public void addResetToken(String email, String token, LocalDateTime expiration) {
        Customer customer = findByEmail(email);
        customer.setResetPasswordToken(token);
        customer.setResetPasswordTokenExpiration(expiration);
        persist(customer);
    }

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
}
