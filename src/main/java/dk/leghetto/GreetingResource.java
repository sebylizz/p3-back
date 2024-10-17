package dk.leghetto;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
}