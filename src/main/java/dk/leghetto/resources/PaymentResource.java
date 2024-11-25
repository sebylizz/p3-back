package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import dk.leghetto.classes.Order;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.ProductRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
}
