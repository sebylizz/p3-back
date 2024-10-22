package dk.leghetto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public void add() {
        Product p = new Product();
        p.setSize("HUGE");
        p.setPrice(999);
        p.setType("NIG");
        persist(p);
    }
}
