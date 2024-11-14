package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public void add(String name, String size, Long price) {
        Product p = new Product(name, size, price);
        persist(p);
    }

    public void delete(Long id) throws NotFoundException {
        Product p = findById(id);
        delete(p);
    }
}
