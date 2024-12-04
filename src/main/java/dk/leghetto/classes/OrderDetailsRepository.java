package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsRepository implements PanacheRepository<OrderDetails> {
    public Long add(String first_name, String last_name, String address, Integer postalCode, Integer phoneNumber,
            String email, Long userId) {
        OrderDetails o = new OrderDetails();
        o = o.create(first_name, last_name, address, postalCode, phoneNumber, email, userId);
        persist(o);
        return o.getId();
    }
}
