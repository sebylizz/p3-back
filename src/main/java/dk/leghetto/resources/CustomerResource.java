package dk.leghetto.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import dk.leghetto.Customer;
import dk.leghetto.CustomerRepository;
import dk.leghetto.classes.CustomerRequest;

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

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addcustomer")
    public Response addCustomer(
            @RequestBody(description = "Customer details", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CustomerRequest.class))) CustomerRequest customerRequest) {
        customerRepository.add(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getEmail(),
                customerRequest.getPassword());
        return Response.ok().build();
    }
}
