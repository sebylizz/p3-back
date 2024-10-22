package dk.leghetto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Path("/products")
public class ProductResource {
    @Inject
    ProductRepository productRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getproducts")
    public List<Product> listProducts() {
        return productRepository.listAll();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addproduct")
    public Response addProduct(
            @Parameter(description = "Product name", required = true) @QueryParam("name") String name,
            @Parameter(description = "Size", required = true) @DefaultValue("Onesize") @QueryParam("size") String size,
            @Parameter(description = "Price", required = true) @QueryParam("price") Integer price) {
        productRepository.add(name, size, price);
        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteproduct/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            productRepository.delete(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(404).build();
        }
    }
}
