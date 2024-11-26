package dk.leghetto.resources;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.services.CustomerRequest;
import dk.leghetto.services.MailService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    @Inject
    MailService mailService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getcustomers")
    public List<Customer> listCustomers(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        PanacheQuery<Customer> query = customerRepository.findAll();
        return query.page(offset / limit, limit).list(); // Fetch paginated customers
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addcustomer")
    public Response addCustomer(CustomerRequest customerRequest) {

        if (customerRepository.findByEmail(customerRequest.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("email already in use")
                    .build();
        }

        String verificationToken = UUID.randomUUID().toString();
        String verificationLink = "http://localhost:3000/verification?token=" + verificationToken;

        customerRepository.add(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getEmail(),
                customerRequest.getTelephone(),
                customerRequest.getAddress(),
                customerRequest.getPostalCode(),
                customerRequest.getPassword(),
                verificationToken,
                false); //for at sætte verified

        String body = "Hello " + customerRequest.getFirstName() + ",\n\n"
                + "Welcome to Leghetto! We are delighted to have you join our community.\n\n"
                + "To complete your registration and verify your account, please click the link below:\n\n"
                + verificationLink + "\n\n"
                + "If you did not create this account, please disregard this email.\n\n"
                + "Best regards,\n"
                + "The Leghetto Team";

        mailService.sendMail(customerRequest.getEmail(), "Welcome to Leghetto", body);
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

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/verify")
    public Response verifyAccount(@QueryParam("token") String token) {
        Customer customer = customerRepository.findByVerificationToken(token);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid or expired verification token")
                    .build();
        }

        customer.setVerified(true);
        customerRepository.persist(customer);

        return Response.ok("Verified").build();
    }

    @Transactional
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteCustomer")
    @RolesAllowed({"user", "admin"})
    public Response deleteCustomer(@Context SecurityContext ctx, @QueryParam("Id") Long Id) {
        try {
            if (Id == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Customer ID must be provided.")
                        .build();
            }
            Customer targetCustomer = customerRepository.findById(Id);
            Customer userCustomer = customerRepository.findByEmail(ctx.getUserPrincipal().getName());
            if (targetCustomer == null || userCustomer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found")
                        .build();
            }
            if (Objects.equals(targetCustomer.getId(), userCustomer.getId()) || ctx.isUserInRole("admin")) {
                customerRepository.delete(Id);
                return Response.ok("user deleted").build();
            }
        } catch(Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while deleting the customer: " + e.getMessage())
                    .build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity("No permission to delete the customer")
                .build();
    }

}
