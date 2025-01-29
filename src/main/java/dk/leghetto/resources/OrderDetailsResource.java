package dk.leghetto.resources;

import dk.leghetto.classes.OrderDetails;
import dk.leghetto.classes.OrderDetailsRepository;
import dk.leghetto.classes.OrderSummaryDTO; // New DTO class
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Path("/orderDetails")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderDetailsResource {

    @Inject
    OrderDetailsRepository orderDetailsRepository;

    @GET
    @Path("/getcustomerorders")
    @RolesAllowed("user")
    public Response orderDetails(@Context SecurityContext ctx) {
        // Fetch the logged-in user's email from the security context
        String email = ctx.getUserPrincipal().getName();

        // Fetch orders associated with this email
        List<OrderDetails> orders = orderDetailsRepository.find("email", email).list();
        if (orders.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "No orders found for the provided email.", "email", email))
                    .build();
        }

        // Transform orders into a simpler DTO for the response
        List<OrderSummaryDTO> orderResponse = orders.stream().map(order -> {
            // Calculate total price for the order
            double totalPrice = order.getOrderItems().stream()
                    .mapToDouble(item -> item.getPrice())
                    .sum();

            // Concatenate product names
            String productNames = order.getOrderItems().stream()
        .filter(item -> item.getProductVariant() != null && item.getProductVariant().getProduct() != null)
        .map(item -> item.getProductVariant().getProduct().getName())
        .collect(Collectors.joining(", "));

            return new OrderSummaryDTO(
                    order.getId(),
                    productNames,
                    totalPrice
            );
        }).collect(Collectors.toList());

        return Response.ok(orderResponse).build();
    }
}
