package dk.leghetto.classes;

import java.util.List;
import java.util.stream.Collectors;

import dk.leghetto.classes.ProductDTO.ColorDTO;
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
                    productDTO.setPrice(product.getPrice().getPrice());
                    productDTO.setColors(product.getVariants().stream()
                            .map(variant -> {
                                ColorDTO colorDTO = new ColorDTO();
                                colorDTO.setId(variant.getColor().getId());
                                colorDTO.setName(variant.getColor().getColor().getName());
                                colorDTO.setMainImage(variant.getColor().getMainImage());
                                colorDTO.setImages(variant.getColor().getImages());
                                colorDTO.setVariants(variant.getColor().getVariants().stream()
                                        .map(colorVariant -> {
                                            ColorDTO.VariantDTO variantDTO = new ColorDTO.VariantDTO();
                                            variantDTO.setId(colorVariant.getId());
                                            variantDTO.setSize(colorVariant.getSize().getName());
                                            variantDTO.setQuantity(colorVariant.getQuantity());
                                            return variantDTO;
                                        }).collect(Collectors.toList()));
                                                               
                                return colorDTO;
                            }).collect(Collectors.toList()));
                    productDTO.setMainImage(product.getVariants().get(0).getColor().getMainImage());
                    return productDTO;
                }).collect(Collectors.toList());
    }
}
