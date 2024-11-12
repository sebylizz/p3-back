package dk.leghetto.resources;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.schemas.CustomerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getsinglecustomer")
    @RolesAllowed("user")
    public Response customer(@Context SecurityContext ctx) {
        return Response.ok(customerRepository.findByEmail(ctx.getUserPrincipal().getName())).build();
    }

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
    @POST
    @Path("/updateCustomer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(Customer customer) {
        try {
            // Use getters to access fields
            Customer updatedCustomer = Customer.updateCustomer(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail());
            return Response.ok(updatedCustomer).build();  // Return updated customer as response
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating customer: " + e.getMessage())
                    .build();
        }
    }
}
