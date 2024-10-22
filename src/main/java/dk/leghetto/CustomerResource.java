package dk.leghetto;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/customers")
public class CustomerResource {
    @Inject
    CustomerRepository customerRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getcustomers")
    public List<Customer> listPersons() {
        return customerRepository.listAll();
    }
}
