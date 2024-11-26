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
                    productDTO.setColors(product.getColors().stream()
                            .map(color -> {
                                ColorDTO colorDTO = new ColorDTO();
                                colorDTO.setId(color.getId());
                                colorDTO.setName(color.getColor().getName());
                                colorDTO.setMainImage(color.getMainImage());
                                colorDTO.setImages(color.getImages());
                                colorDTO.setVariants(color.getVariants().stream()
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
