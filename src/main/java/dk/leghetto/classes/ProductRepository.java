package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public void add(String name, String size, Double price, Integer quantity, String image, String mainImage) {
        Product p = new Product(name, size, price, quantity, image, mainImage);
        persist(p);
    }

    public void delete(Long id) throws NotFoundException {
        Product p = findById(id);
        delete(p);
    }
    public Product findById2(Long id) {
        Product p = findById(id);
        return p;
    }
}
