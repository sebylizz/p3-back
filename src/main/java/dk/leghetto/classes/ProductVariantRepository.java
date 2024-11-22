package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductVariantRepository implements PanacheRepository<ProductVariant> {
    public ProductVariantDTO getDTO(Long id) {
        ProductVariant product = findById(id);
        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setId(product.getId());
        dto.setPrice(product.getProduct().getPrice().getPrice());
        dto.setName(product.getProduct().getName());
        return dto;
    }
}
