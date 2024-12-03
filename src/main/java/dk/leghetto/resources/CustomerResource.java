package dk.leghetto.resources;

import java.util.List;
import java.util.Map;
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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.services.CustomerRequest;
import dk.leghetto.services.MailService;
import dk.leghetto.services.MatchPasswordRequest;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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

    @RolesAllowed("admin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getcustomers")
    public Response listCustomers(
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit) {

        PanacheQuery<Customer> query = customerRepository.findAll();

        int pageIndex = offset / limit;

        query.page(Page.of(pageIndex, limit));

        List<Customer> customers = query.list();

        return Response.ok(Map.of(
                "customers", customers,
                "total", query.count(), // Total number of customers
                "offset", offset,
                "limit", limit)).build();
    }

    @RolesAllowed("admin")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteCustomer/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {
            customerRepository.delete(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found with ID: " + id)
                    .build();
        }
    }

    @RolesAllowed("admin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCustomerById/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Customer customer = Customer.findById(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer not found with ID: " + id)
                    .build();
        }
        return Response.ok(customer).build();
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
                customerRequest.getPassword(),
                verificationToken,
                false); // for at sætte verified

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
            Customer updatedCustomer = Customer.updateCustomer(customer.getId(), customer.getFirstName(),
                    customer.getLastName(), customer.getEmail());
            return Response.ok(updatedCustomer).build(); // Return updated customer as response
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

    @RolesAllowed("admin")
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addCustomerAdmin")
    public Response addCustomerAdmin(CustomerRequest customerRequest) {
        if (customerRepository.findByEmail(customerRequest.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email already in use")
                    .build();
        }

        if (!"user".equals(customerRequest.getRole()) && !"admin".equals(customerRequest.getRole())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid role specified")
                    .build();
        }

        if (!customerRepository.checkCustomerPassword(customerRequest.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Password does not meet complexity requirements")
                    .build();
        }

        customerRepository.addAdmin(
                customerRequest.getFirstName(),
                customerRequest.getLastName(),
                customerRequest.getEmail(),
                customerRequest.getPassword(),
                customerRequest.getRole());

                return Response.status(Response.Status.CREATED)
                .entity("Customer added successfully")
                .build();
    }

    @RolesAllowed("admin")
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCustomers(
            @QueryParam("query") String query,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("10") int limit) {

        if (query == null || query.trim().isEmpty()) {
            throw new WebApplicationException("Search query cannot be empty", Response.Status.BAD_REQUEST);
        }

        PanacheQuery<Customer> searchQuery = customerRepository.find(
                "LOWER(firstName) LIKE :query OR LOWER(email) LIKE :query OR LOWER(lastName) LIKE :query",
                Parameters.with("query", "%" + query.trim().toLowerCase() + "%"));

        searchQuery.page(Page.of(offset / limit, limit));
        List<Customer> paginatedCustomers = searchQuery.list();

        return Response.ok(Map.of(
                "customers", paginatedCustomers,
                "total", searchQuery.count(),
                "pageCount", searchQuery.pageCount(),
                "offset", offset,
                "limit", limit)).build();
    }

    @Transactional
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteCustomer")
    @RolesAllowed({ "user", "admin" })
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
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while deleting the customer: " + e.getMessage())
                    .build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity("No permission to delete the customer")
                .build();
    }

    @RolesAllowed("admin")
    @PUT
    @Path("/updateCustomer/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateCustomerById(@PathParam("id") Long id, Customer customer) {
        try {

            Customer existingCustomer = Customer.findById(id);
            if (existingCustomer == null) {
                throw new NotFoundException("Customer not found with ID: " + id);
            }

            Customer otherCustomerWithEmail = Customer.find("email = ?1", customer.getEmail()).firstResult();
            if (otherCustomerWithEmail != null && !otherCustomerWithEmail.getId().equals(id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Email already in use by another customer.")
                        .build();
            }

            existingCustomer.setFirstName(customer.getFirstName());
            existingCustomer.setLastName(customer.getLastName());
            existingCustomer.setAddress(customer.getaddress());
            existingCustomer.setPostalCode(customer.getPostalCode());
            existingCustomer.setTelephone(customer.getTelephone());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setNewsletter(customer.getNewsletter());
            existingCustomer.setRole(customer.getRole());

            return Response.ok(existingCustomer).build();
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

    @POST
    @Path("/matchPassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response matchPassword(@Context SecurityContext ctx, MatchPasswordRequest request) {
        Customer email = customerRepository.findByEmail(ctx.getUserPrincipal().getName());

        if (email == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        Long userId = email.getId();
        try {
            Boolean isMatch = customerRepository.matchPassword(request.getPassword(), userId);
            return Response.ok(isMatch).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred: " + e.getMessage())
                    .build();
        }
    }

}
