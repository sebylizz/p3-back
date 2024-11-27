package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderItemsRepository implements PanacheRepository<OrderItems> {

    public void add(Long orderDetailsId, Long variantId, Long price) {
        OrderItems o = new OrderItems(orderDetailsId, variantId, price);
        persist(o);

    }
}
