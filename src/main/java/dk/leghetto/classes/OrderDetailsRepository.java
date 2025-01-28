package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderDetailsRepository implements PanacheRepository<OrderDetails> {
    public Long add(String firstName, String lastName, String address, Integer postalCode, Integer phoneNumber, String email, Long userId) {
        // Use the factory method to create an instance
        OrderDetails orderDetails = OrderDetails.create(firstName, lastName, address, postalCode, phoneNumber, email, userId);
        persist(orderDetails);
        return orderDetails.getId();
    }

    public OrderDetails findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
