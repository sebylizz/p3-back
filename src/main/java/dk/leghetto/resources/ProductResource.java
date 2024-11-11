package dk.leghetto.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import dk.leghetto.classes.Product;
import dk.leghetto.classes.ProductRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
            @Parameter(description = "Price", required = true) @QueryParam("price") Double price) {
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
