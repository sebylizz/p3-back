package dk.leghetto.resources;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.leghetto.classes.Category;
import dk.leghetto.classes.Collection;
import dk.leghetto.classes.Colors;
import dk.leghetto.classes.ProductColor;
import dk.leghetto.classes.ProductPrice;
import dk.leghetto.classes.ProductRepository;
import dk.leghetto.classes.ProductRequestDTO;
import dk.leghetto.classes.ProductSize;
import dk.leghetto.classes.ProductVariant;
import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.Product;
import dk.leghetto.classes.Sizes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    @Inject
    ProductRepository pr;

    @Inject
    ProductVariantRepository pvr;

    @Path("/getvariants")
    @GET
    public Response getVariants() {
        return Response.ok(pvr.getDTO(1L)).build();
    }

    @Path("/getall")
    @GET
    public Response getAllProducts() {
        return Response.ok(pr.getAllActiveProducts()).build();
    }

    @Path("/add")
    @POST
    @Transactional
    public Response addProduct(ProductRequestDTO request) {
        try {
            // Validate and fetch Category
            Category category = Category.findById(request.getCategoryId());
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid category ID: " + request.getCategoryId()).build();
            }
    
            // Validate and fetch Collection
            Collection collection = Collection.findById(request.getCollectionId());
            if (collection == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid collection ID: " + request.getCollectionId()).build();
            }
    
            // Persist Product
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setIsActive(request.getIsActive());
            product.setCategory(category);
            product.setCollection(collection);
            product.persist();
    
            // Persist Product Colors
            for (ProductRequestDTO.ColorDTO colorDTO : request.getColors()) {
                Colors color = Colors.findById(colorDTO.getColorId());
                if (color == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid color ID: " + colorDTO.getColorId()).build();
                }
    
                ProductColor productColor = new ProductColor();
                productColor.setProduct(product);
                productColor.setColor(color);
                productColor.setMainImage(colorDTO.getMainImage());
                productColor.setImages(colorDTO.getImages());
                productColor.persist();
            }
    
            // Persist Product Price
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(product);
            productPrice.setPrice(request.getPrice().getPrice());
            productPrice.setIsDiscount(request.getPrice().getIsDiscount());
            productPrice.setStartDate(request.getPrice().getStartDate());
            productPrice.setEndDate(request.getPrice().getEndDate());
            productPrice.persist();
    
            // Persist Product Variants
            for (ProductRequestDTO.VariantDTO variantDTO : request.getVariants()) {
                ProductColor productColor = ProductColor.find("product = ?1 and color.id = ?2",
                        product, variantDTO.getColorId()).firstResult();
                if (productColor == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid color ID for variant: " + variantDTO.getColorId()).build();
                }
    
                ProductSize size = ProductSize.findById(variantDTO.getSizeId());
                if (size == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid size ID for variant: " + variantDTO.getSizeId()).build();
                }
    
                ProductVariant productVariant = new ProductVariant();
                productVariant.setProduct(product);
                productVariant.setColor(productColor);
                productVariant.setSize(size);
                productVariant.setQuantity(variantDTO.getQuantity());
                productVariant.persist();
            }
    
           // Return the product ID to the frontend
           System.out.println(product.getId());        
           return Response.status(Response.Status.CREATED)
        .entity(Map.of("productId", product.getId())) // Return as JSON object
        .build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding product: " + e.getMessage()).build();
        }
    }
    
    
}
