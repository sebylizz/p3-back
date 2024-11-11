package dk.leghetto.resources;

import java.nio.channels.SelectableChannel;
import java.util.List;

import dk.leghetto.services.MailService;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.classes.CustomerRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {
    @Inject
    CustomerRepository customerRepository;
    @Inject
    MailService mailService;

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

        if (customerRepository.findByEmail(customerRequest.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("email already in use")
                           .build();
        }

        customerRepository.add(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getEmail(),
                customerRequest.getPassword());
        mailService.sendMail(customerRequest.getEmail(), "Welcome to Leghetto", "We are pleased to welcome you in Leghetto " + customerRequest.getFirstName() + " with this emailaddress: " + customerRequest.getEmail() + "\nPlease click this link to verify your account: ");
        return Response.ok().build();
    }
}
