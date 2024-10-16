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
    PersonRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> list() {
        return repository.listAll();
    }

}
