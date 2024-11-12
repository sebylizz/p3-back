package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    public void add(String first_name, String last_name, String email, String password) {
        Customer p = new Customer(first_name, last_name, email, password);
        persist(p);
    }

    public void delete(Long id) throws NotFoundException {
        Customer p = findById(id);
        delete(p);
    }

    public Customer findByEmail(String email) {
        return find("email", email).firstResult();
    }

}
