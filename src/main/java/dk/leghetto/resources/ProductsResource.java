package dk.leghetto.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.tax.Registration.CountryOptions.Co;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.leghetto.classes.Category;
import dk.leghetto.classes.Collection;
import dk.leghetto.classes.Colors;
import dk.leghetto.classes.ProductColor;
import dk.leghetto.classes.ProductPrice;
import dk.leghetto.classes.ProductRequestDTO;
import dk.leghetto.classes.ProductVariant;
import dk.leghetto.classes.Products;
import dk.leghetto.classes.Sizes;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductsResource {
    @POST
    @Transactional
    public Response addProduct(ProductRequestDTO request) {
        // Step 1: Validate and Fetch Related Entities
        // Category category = Category.findById(request.getCategoryId());
        // if (category == null) {
        //     return Response.status(Response.Status.BAD_REQUEST)
        //             .entity("Invalid category ID: " + request.getCategoryId()).build();
        // }

        Collection collection = Collection.findById(request.getCollectionId());
        if( collection == null){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("Invalid category ID: " + request.getCollectionId()).build();

        }

        // Step 2: Persist Product
        Products product = new Products();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setIsActive(request.getIsActive());
        // product.setCategory(category); 
        product.setCollection(collection); 
        product.persist();

        // Step 3: Persist Product Colors
        for (ProductRequestDTO.ColorDTO colorDTO : request.getColors()) {
            ProductColor productColor = new ProductColor();
            productColor.setProduct(product);

            Colors color = Colors.findById(colorDTO.getColorId());
            if (color == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid color ID: " + colorDTO.getColorId()).build();
            }

            productColor.setColor(color);
            productColor.setMainImage(colorDTO.getMainImage());
            ObjectMapper mapper = new ObjectMapper();
        String jsonImages;
        try {
            jsonImages = mapper.writeValueAsString(colorDTO.getImages());
            productColor.setImages(jsonImages);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
            productColor.persist();
        }

        // Step 4: Persist Product Price
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setPrice(request.getPrice().getPrice());
        productPrice.setIsDiscount(request.getPrice().getIsDiscount()); 
        productPrice.setStartDate(request.getPrice().getStartDate());
        productPrice.setEndDate(request.getPrice().getEndDate());
        productPrice.persist();

        // Step 5: Persist Product Variants
        for (ProductRequestDTO.VariantDTO variantDTO : request.getVariants()) {
            ProductColor productColor = ProductColor.find("product = ?1 and color.id = ?2",
                    product, variantDTO.getColorId()).firstResult();
            Sizes size = Sizes.findById(variantDTO.getSizeId());
            if (productColor == null || size == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid color ID or size ID in variant").build();
            }

            ProductVariant productVariant = new ProductVariant();
            productVariant.setProduct(product);
            productVariant.setColor(productColor.getColor());
            productVariant.setSize(size);
            productVariant.setQuantity(variantDTO.getQuantity());
            productVariant.persist();
        }

        return Response.status(Response.Status.CREATED).entity(product).build();
    }
    
}
