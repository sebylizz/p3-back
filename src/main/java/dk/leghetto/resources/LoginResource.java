package dk.leghetto.resources;

import java.time.LocalDateTime;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.classes.ForgotPasswordRequest;
import dk.leghetto.classes.LoginRequest;
import dk.leghetto.classes.ResetPasswordRequest;
import dk.leghetto.classes.Token;
import dk.leghetto.services.MailService;
import dk.leghetto.services.TokenService;
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
    TokenService tokenService;

    @Inject
    MailService mailService;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email and password must not be null").build();
        }

        Customer customer = customerRepository.findByEmail(email);
        if (customer != null && BcryptUtil.matches(password, customer.getPasswordHash())) {
            String token = tokenService.generateToken(email);

            return Response.ok().header("Set-Cookie", "token="+token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password").build();
    }

    @GET
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
            return Response.ok().header("Set-Cookie", "token=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Max-Age=0; Path=/;").build();
    }

    @POST
    @Path("/refresh")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public Response refresh() {
        String email = jwtWebToken.getName();
        String token = tokenService.generateToken(email);
        return Response.ok().entity(new Token(token)).build();
    }

    @POST
    @Path("/forgot")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(
            @RequestBody(description = "Mail of forgetful user", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ForgotPasswordRequest.class))) ForgotPasswordRequest forgotPasswordRequest) {

        String email = forgotPasswordRequest.getEmail();
        Customer customer = customerRepository.findByEmail(email);

        if (customer != null) {
            String resetToken = UUID.randomUUID().toString();
            String resetLink = "http://localhost:3000/reset_password?token=" + resetToken;
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(30); // expiration på 30 min fra generering

            customerRepository.addResetToken(email, resetToken, expiration);

            String body = "Hello " + customer.getFirstName() + ",\n\n"
                    + "We received a request to reset your password for your Leghetto account. "
                    + "If you made this request, please click the link below to set a new password:\n\n"
                    + resetLink + "\n\n"
                    + "If you did not request to reset your password, you can safely ignore this email. "
                    + "Your password will remain unchanged.\n\n"
                    + "Thank you,\n"
                    + "The Leghetto Team";

            mailService.sendMail(email, "Forgot password at Leghetto", body);
        }
        return Response.ok().build();
    }

    @POST
    @Path("/reset-password")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(ResetPasswordRequest resetPasswordRequest) {

        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getPassword();

        Customer customer = customerRepository.findByResetPasswordToken(token);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid reset token")
                    .build();
        }

        if (customer.getResetPasswordTokenExpiration() == null || customer.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now())) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reset token has expired")
                    .build();
        }

        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        customer.setResetPasswordTokenExpiration(null);
        customer.persist();

        return Response.ok("Password reset!").build();
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
    @RolesAllowed("user")
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
