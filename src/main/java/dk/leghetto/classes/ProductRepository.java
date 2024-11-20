package dk.leghetto.classes;

import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public List<ProductDTO> getAllActiveProducts() {
        List<Product> products = find("isActive = true").list();
        return products.stream()
                .map(product -> {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setId(product.getId());
                    productDTO.setName(product.getName());
                    productDTO.setDescription(product.getDescription());
                    productDTO.setCategoryId(product.getCategory().getId());
                    productDTO.setCollectionId(product.getCollection().getId());
                    productDTO.setMainImage(product.getColors().getFirst().getMainImage());
                    productDTO.setColors(product.getColors());
                    productDTO.setPrice(product.getPrice().getPrice());
                    return productDTO;
                })
                .collect(Collectors.toList());
    }
}
