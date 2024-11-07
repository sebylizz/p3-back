package dk.leghetto.resources;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.classes.ForgotPasswordRequest;
import dk.leghetto.classes.LoginRequest;
import dk.leghetto.classes.Token;
import dk.leghetto.classes.TokenGenerator;
import dk.leghetto.services.MailService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/login")
public class LoginResource {
    @Inject
    CustomerRepository customerRepository;

    @Inject
    TokenGenerator tokenGenerator;

    @Inject
    MailService mailService;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @RequestBody(description = "Login request containing email and password", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LoginRequest.class))) LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email and password must not be null").build();
        }

        Customer customer = customerRepository.findByEmail(email);
        if (customer != null && BcryptUtil.matches(password, customer.getPasswordHash())) {
            String token = tokenGenerator.generateToken(email);
            return Response.ok().entity(new Token(token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password").build();
    }

    @POST
    @Path("/refresh")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response refresh() {
        String email = jwtWebToken.getName();
        String token = tokenGenerator.generateToken(email);
        return Response.ok().entity(new Token(token)).build();
    }

    @POST
    @Path("/forgot")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(
            @RequestBody(description = "Mail of forgetful user", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ForgotPasswordRequest.class))) ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            mailService.sendMail(email, "Forgot password at Leghetto", "Hello Mr. " + customer.getLastName());
        }
        return Response.ok().build();
    }

    @Inject
    JsonWebToken jwtWebToken;

    // Everything below is temporary for dev only
    @GET
    @Path("all-allowed")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String allowAll(@Context SecurityContext ctx) {
        return getResponse(ctx);
    }

    @GET
    @Path("users-allowed")
    @RolesAllowed({ "user" })
    @Produces(MediaType.TEXT_PLAIN)
    public String usersAllowed(@Context SecurityContext ctx) {
        return getResponse(ctx);
    }

    @GET
    @Path("admins-allowed")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String adminsAllowed(@Context SecurityContext ctx) {
        return getResponse(ctx);
    }

    private String getResponse(SecurityContext ctx) {
        String name = "anon";
        System.out.println(jwtWebToken.getClaimNames());
        if (jwtWebToken.getClaimNames() != null && jwtWebToken.getClaimNames().contains("upn")) {
            String email = jwtWebToken.getName();
            Customer customer = customerRepository.findByEmail(email);
            if (customer != null) {
                name = customer.getFirstName();
            }
        }
        return String.format("Hi %s", name);
    }
}
