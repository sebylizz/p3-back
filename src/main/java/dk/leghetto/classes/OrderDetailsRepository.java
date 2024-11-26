package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsRepository implements PanacheRepository<OrderDetails> {

    public void add(String first_name, String last_name, String address, Integer postalCode, Integer phoneNumber, String email) {
        OrderDetails o = new OrderDetails(first_name, last_name, address, postalCode, phoneNumber, email);
        persist(o);
    }
}

