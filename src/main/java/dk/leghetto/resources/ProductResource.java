package dk.leghetto.resources;

import java.util.List;
import java.util.Map;
import jakarta.annotation.security.RolesAllowed;

import dk.leghetto.classes.ProductGetPostDTO;
import dk.leghetto.classes.ProductRepository;
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
    public Response addProduct(ProductGetPostDTO request) {
        try {
            Map<String, Object> result = pr.addProduct(request);
            return Response.status(Response.Status.CREATED).entity(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
            ProductGetPostDTO product = pr.getProductWithPricesById(productId);
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
    public Response updateProduct(@PathParam("id") Long productId, ProductGetPostDTO productDTO) {
        Product existingProduct = Product.findById(productId);
        if (existingProduct == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }

        pr.updateBasicDetails(existingProduct, productDTO);
        List<Map<String, Long>> newColorMapping;

        try {
            pr.validateAndUpdatePrices(existingProduct, productDTO.getPrices());

            newColorMapping = pr.updateColorsAndVariants(existingProduct, productDTO.getColors());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        existingProduct.persist();

        return Response.ok(newColorMapping).build();
    }

}
