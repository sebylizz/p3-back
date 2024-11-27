package dk.leghetto.resources;

import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.Cart;
import dk.leghetto.classes.OrderDetails;
import dk.leghetto.classes.OrderDetailsRepository;
import dk.leghetto.classes.ProductVariantDTO;
import dk.leghetto.classes.ProductVariant;
import dk.leghetto.classes.OrderItemsRepository;
import dk.leghetto.services.CartService;
import dk.leghetto.services.MailService;
import dk.leghetto.services.OrderRequest;
import io.quarkus.datasource.runtime.DataSourcesBuildTimeConfig;
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
    ProductVariantRepository pvr;

    @Inject
    PaymentRequest paymentRequest;

    @Inject
    OrderDetailsRepository orderDetailsRepository;

    @Inject
    MailService mailService;

    @Inject
    CartService cartService;

    @Inject
    OrderItemsRepository orderItemsRepository;

    @Inject
    DataSourcesBuildTimeConfig dataSourcesBuildTimeConfig;

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

    // husk at dobbelttjekke confirmed payment
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/confirmOrder")
    public Response confirmOrder(OrderRequest orderRequest) {

        OrderDetails orderDetails = orderRequest.getDetails();
        Cart cart = cartService.cartFromString(orderRequest.getProductIds());

        Long id = orderDetailsRepository.add(
                orderDetails.getFirstName(),
                orderDetails.getLastName(),
                orderDetails.getAddress(),
                orderDetails.getPostalCode(),
                orderDetails.getPhoneNumber(),
                orderDetails.getEmail()
        );

        for (ProductVariantDTO variant : cart.getItems()) {
            Long orderDetailsId = id;
            Long variantId = variant.getId();
            Long price = variant.getPrice();
            orderItemsRepository.add(
                    orderDetailsId,
                    variantId,
                    price
            );
            ProductVariant pv = pvr.findById(variantId);
            try {
                Long currentQuantity = pv.getQuantity();
                if(currentQuantity <= 0) {
                    throw new Exception("No more product left");
                }
                if (currentQuantity == 5) {
                    mailService.sendMail("ekkr", "Low stock", "Faggets der er low stock på denne vare: " + pv.getProduct().getName());
                }
                if (currentQuantity == 1) {
                    mailService.sendMail("ekkr", "Out of stock", "Faggets denne vare er out of stock: " + pv.getProduct().getName());
                }
                pv.setQuantity(currentQuantity - 1);
            } catch(Exception e) {
                return Response.serverError().entity(e).build();
            }
            pv.persist();
        }

        String body = "Hallo bøsser i har fået en ny ordre til " + orderDetails.getFirstName() + ",\n\n"
                + "Ordre detaljer:\n\n"
                + "First name: " + orderDetails.getFirstName() + "\n"
                + "Last name: " + orderDetails.getLastName() + "\n"
                + "Adresse: " + orderDetails.getAddress() + "\n"
                + "Post nummer: " + orderDetails.getPostalCode() + "\n"
                + "Telefonnummer: " + orderDetails.getPhoneNumber() + "\n"
                + "Email: " + orderDetails.getEmail() + "\n\n"
                + "Ordre indhold:\n\n"
                + cart.getItems().getFirst().getName() + "\n\n"
                + "Send den afsted faggets!";

        mailService.sendMail("ekkr", "New order", body);

        return Response.ok(body).build();
    }
}
