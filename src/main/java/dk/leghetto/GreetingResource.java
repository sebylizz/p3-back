package dk.leghetto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/leghetto")
public class GreetingResource {

    @Inject
    PersonRepository personRepository;

    @Inject
    ProductRepository productRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/persons")
    public List<Person> listPersons() {
        return personRepository.listAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/products")
    public List<Product> listProducts() {
        return productRepository.listAll();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/products")
    public Response addProduct() {
        productRepository.add();
        return Response.ok().build();
    }
}
