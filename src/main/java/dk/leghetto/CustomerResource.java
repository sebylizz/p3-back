package dk.leghetto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

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
            @Parameter(description = "First name", required = true) @QueryParam("first_name") String first_name,
            @Parameter(description = "Last name", required = true) @QueryParam("last_name") String last_name,
            @Parameter(description = "Email address", required = true) @QueryParam("email") String email,
            @Parameter(description = "Password", required = true) @QueryParam("password") String password) {
        customerRepository.add(first_name, last_name, email, password);
        return Response.ok().build();
    }

    @GET
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/checklogin")
    public Response login(
            @Parameter(description = "Email address", required = true) @QueryParam("email") String email,
            @Parameter(description = "Password", required = true) @QueryParam("password") String password) {
        Customer c = customerRepository.findByEmail(email);
        if (c != null) {
            if (c.matchPsw(password)) {
                return Response.ok().build();
            }
        }
        return Response.status(404).build();
    }

}
