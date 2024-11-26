package dk.leghetto.resources;

import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.Cart;
import dk.leghetto.classes.OrderDetails;
import dk.leghetto.classes.OrderDetailsRepository;
import dk.leghetto.services.CartService;
import dk.leghetto.services.MailService;
import dk.leghetto.services.OrderRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/productpayment")
public class PaymentResource {
    @Inject
    ProductVariantRepository productVariantRepository;

    @Inject
    PaymentRequest paymentRequest;

    @Inject
    OrderDetailsRepository orderDetailsRepository;

    @Inject
    MailService mailService;

    @Inject
    CartService cartService;

    /*
     * @POST
     * 
     * @Transactional
     * 
     * @Consumes(MediaType.APPLICATION_JSON)
     * 
     * @Path("/carttest")
     * public Response cart(List<Long> variantIds) throws StripeException {
     * 
     * Order order = new Order();
     * 
     * for (Long variantId : variantIds) {
     * ProductVariantDTO productVariant =
     * productVariantRepository.getDTO(variantId);
     * if (productVariant != null) {
     * order.addProduct(productVariant);
     * }
     * else {
     * return Response.status(Response.Status.NOT_FOUND)
     * .entity("Product variant not found.")
     * .build();
     * }
     * }
     * 
     * String paymentLink = paymentRequest.paymentRequest(order);
     * 
     * return Response.ok(paymentLink).build();
     * }
     */

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/generateSessionId")
    public Response generateSessionId(String productIds) {
        Cart cart = cartService.cartFromString(productIds);
        try {
            String sessionId = paymentRequest.generateSessionId(cart);
            return Response.ok(sessionId).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/confirmOrder")
    public Response confirmOrder(OrderRequest orderRequest) {

        OrderDetails orderDetails = orderRequest.getDetails();
        Cart cart = orderRequest.getCart();

        String body = "Hallo bøsser i har fået en ny ordre til " + orderDetails.getFirstName() + ",\n\n"
                + "Ordre detaljer:\n\n"
                + "First name: " + orderDetails.getFirstName() + "\n"
                + "Last name: " + orderDetails.getLastName() + "\n"
                + "Adresse: " + orderDetails.getAddress() + "\n"
                + "Post nummer: " + orderDetails.getPostalCode() + "\n"
                + "Telefonnummer: " + orderDetails.getPhoneNumber() + "\n"
                + "Email: " + orderDetails.getEmail() + "\n\n"
                + "Ordre indhold:\n\n"
                + cart.toString() + "\n\n"
                + "Send den afsted faggets!";

        // mailService.sendMail("emil624g@gmail.com", "New order", body);

        return Response.ok(body).build();
    }
}
