package dk.leghetto.resources;

import dk.leghetto.classes.OrderDetails;
import dk.leghetto.classes.OrderDetailsRepository;
import dk.leghetto.classes.OrderDTO;
import dk.leghetto.classes.OrderItemDTO;
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

        // Transform orders into OrderDTOs
        List<OrderDTO> orderResponse = orders.stream().map(order -> {
            // Map order items to OrderItemDTOs (assuming order.getOrderItems() returns the items)
            List<OrderItemDTO> items = order.getOrderItems().stream()
                    .map(item -> new OrderItemDTO(
                            item.getProductVariant().getProduct().getName(),
                            item.getProductVariant().getSize().getName(),
                            item.getProductVariant().getColor().toString(),
                            item.getPrice()
                    ))
                    .collect(Collectors.toList());

            return new OrderDTO(
                    order.getId(),
                    order.getFirstName() + " " + order.getLastName(),
                    order.getAddress(),
                    order.getPostalCode(),
                    order.getPhoneNumber(),
                    order.getEmail(),
                    items
            );
        }).collect(Collectors.toList());

        return Response.ok(orderResponse).build();
    }
}
