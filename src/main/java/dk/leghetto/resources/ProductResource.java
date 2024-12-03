package dk.leghetto.resources;

import java.util.Map;
import jakarta.annotation.security.RolesAllowed;

import dk.leghetto.classes.Category;
import dk.leghetto.classes.Collection;
import dk.leghetto.classes.Colors;
import dk.leghetto.classes.ProductColor;
import dk.leghetto.classes.ProductPrice;
import dk.leghetto.classes.ProductPricesDTO;
import dk.leghetto.classes.ProductRepository;
import dk.leghetto.classes.ProductRequestDTO;
import dk.leghetto.classes.ProductSize;
import dk.leghetto.classes.ProductVariant;
import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.Product;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

    @Inject
    EntityManager entityManager;

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

    @RolesAllowed("admin")
    @Path("/getAllAdmin")
    @GET
    public Response getAllProductsActive() {
        return Response.ok(pr.getAllProducts()).build();
    }

    @RolesAllowed("admin")
    @Path("/add")
    @POST
    @Transactional
    public Response addProduct(ProductRequestDTO request) {
        try {
            Category category = Category.findById(request.getCategoryId());
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid category ID: " + request.getCategoryId()).build();
            }

            Collection collection = Collection.findById(request.getCollectionId());
            if (collection == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid collection ID: " + request.getCollectionId()).build();
            }

            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setIsActive(request.getIsActive());
            product.setCategory(category);
            product.setCollection(collection);
            product.persist();

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

            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(product);
            productPrice.setPrice(request.getPrice().getPrice());
            productPrice.setIsDiscount(request.getPrice().getIsDiscount());
            productPrice.setStartDate(request.getPrice().getStartDate());
            productPrice.setEndDate(request.getPrice().getEndDate());
            productPrice.persist();

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
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("productId", product.getId()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding product: " + e.getMessage()).build();
        }
    }

    @RolesAllowed("admin")
    @GET
    @Path("/modifyProduct/{id}")
    public Response getProductWithPricesById(@PathParam("id") Long productId) {
        try {
            ProductPricesDTO product = pr.getProductWithPricesById(productId);
            return Response.ok(product).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred: " + e.getMessage())
                    .build();
        }
    }

    @RolesAllowed("admin")
    @PUT
    @Path("/updateProduct/{id}")
    @Transactional
    public Response updateProduct(@PathParam("id") Long productId, ProductPricesDTO productDTO) {
        Product existingProduct = Product.findById(productId);
        if (existingProduct == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }

        pr.updateBasicDetails(existingProduct, productDTO);

        try {
            pr.validateAndUpdatePrices(existingProduct, productDTO.getPrices());

            pr.updateColorsAndVariants(existingProduct, productDTO.getColors());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        existingProduct.persist();

        ProductPricesDTO updatedProduct = pr.getProductWithPricesById(existingProduct.getId());
        return Response.ok(updatedProduct).build();
    }

}
