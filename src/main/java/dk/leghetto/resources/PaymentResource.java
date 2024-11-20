package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import dk.leghetto.classes.Order;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.Product;
import dk.leghetto.classes.ProductRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/productpayment")
public class PaymentResource {
    @Inject
    ProductRepository productRepository;

    @Inject
    PaymentRequest paymentRequest;

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

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/carttest")
    public Response cart(List<Long> productIds) throws StripeException {

        Order order = new Order();

        for (Long productId : productIds) {
            Product product = productRepository.findById(productId);
            if (product != null) {
                order.addProduct(product);
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found.")
                        .build();
            }
        }

        String paymentLink = paymentRequest.paymentRequest(order);

        return Response.ok(paymentLink).build();
    }
}
