package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import dk.leghetto.classes.PaymentConfirmationRequest;
import dk.leghetto.classes.ProductVariantDTO;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.Order;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/productpayment")
public class PaymentResource {

    @Inject
    ProductVariantRepository productVariantRepository;

    @Inject
    PaymentRequest paymentRequest;

    // Logger instance to log payment status and errors
    private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    static {
        // Initialize Stripe API key
        com.stripe.Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/paymentConfirmation")
    public Response paymentConfirmation(PaymentConfirmationRequest request) {

        // Extract the PaymentIntent ID from the request
        String paymentIntentId = request.getPaymentIntentId();

        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("PaymentIntent ID is required")
                    .build();
        }

        try {
            // Retrieve the PaymentIntent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String statusMessage;

            if ("succeeded".equals(paymentIntent.getStatus())) {
                statusMessage = "Payment successfully completed.";
                logger.info("Payment successfully completed: {}", paymentIntentId);
            } else {
                statusMessage = "Payment failed or incomplete.";
                logger.warn("Payment failed or incomplete: {}", paymentIntentId);
            }

            return Response.ok(statusMessage).build();

        } catch (StripeException e) {
            logger.error("Stripe exception occurred: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error in payment confirmation process: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/carttest")
    public Response cart(List<Long> variantIds) throws StripeException {

        Order order = new Order();

        for (Long variantId : variantIds) {
            ProductVariantDTO productVariant = productVariantRepository.getDTO(variantId);
            if (productVariant != null) {
                order.addProduct(productVariant);
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product variant not found.")
                        .build();
            }
        }

        // Create payment link for the order
        String paymentLink = paymentRequest.paymentRequest(order);
        return Response.ok(paymentLink).build();
    }
}
