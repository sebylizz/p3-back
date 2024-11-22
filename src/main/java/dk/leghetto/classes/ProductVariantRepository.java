package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductVariantRepository implements PanacheRepository<ProductVariant> {
}
