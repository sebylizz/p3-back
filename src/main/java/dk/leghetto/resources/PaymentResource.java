package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentLink;
import dk.leghetto.classes.PaymentConfirmationRequest;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.ProductRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/productpayment")
public class PaymentResource {
    @Inject
    ProductRepository productRepository;

    @Inject
    PaymentRequest paymentRequest;
    /*
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/getitem")
        public Response test() throws StripeException {
            Order test = new Order();
            test.addProduct(productRepository.findById(1L));
            test.addProduct(productRepository.findById(29L));
            test.addProduct(productRepository.findById(1L));

            paymentRequest.paymentRequest(test);

            return Response.ok().build();
        }
        */

    //Logger instance to log payment status and errors
    private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    static {
        com.stripe.Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/paymentConfirmation")
    public Response paymentConfirmation (PaymentConfirmationRequest request) {

        //The Paymentlink is extracted from request
        String paymentIntentId = request.getPaymentIntentId();

        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("PaymentIntent Id is required")
                    .build();
        }

        try {

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String statusMessage;
                if ("succeeded".equals(paymentIntent.getStatus())) {
                    statusMessage = "Payment successfully completed";
                    logger.info("Payment successfully completed : " + paymentIntentId);
                } else {
                    statusMessage = "Payment failed or incomplete";
                    logger.warn("Payment failed or incomplete: " + paymentIntentId);
                } return Response.ok(statusMessage).build();

            } catch (StripeException e) {
                logger.error("Stripe exception: {}", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("error in payment confirmation process: " + e.getMessage())
                    .build();
        }
    }
}