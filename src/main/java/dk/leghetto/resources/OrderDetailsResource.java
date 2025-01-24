package dk.leghetto.resources;

import dk.leghetto.classes.OrderDetails;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/orderDetails")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderDetailsResource {

    @GET
    @Path("/by-email")
    @RolesAllowed("user")
    public Response getOrdersByEmail(@QueryParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email parameter is required.")
                    .build();
        }

        List<OrderDetails> orders = OrderDetails.find("email = :email", Parameters.with("email", email)).list();

        if (orders.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "No orders found for the provided email.", "email", email))
                    .build();
        }

        return Response.ok(Map.of("orders", orders)).build();
    }
}
