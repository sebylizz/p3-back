package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import dk.leghetto.classes.Order;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.Product;
import dk.leghetto.classes.ProductRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/productpayment")
public class PaymentResource {
    @Inject
    ProductRepository productRepository;

    @Inject
    PaymentRequest paymentRequest;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getitem")

    //A comma seperated id-list
    public Response cart(@QueryParam("productIds") List<Integer> productIds) throws StripeException {

        Order cart = new Order();

        //A Loop that goes through every Product-id from queryparameter
        for (Integer productId : productIds) {
            Optional <Product> productOpt = Optional.ofNullable(productRepository.findById(Long.valueOf(productId)));
            if (productOpt.isPresent()) {
                cart.addProduct(productOpt.get());
            //If the id is not found, then a 404 is returned
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product id: " + productId + "Was not found")
                        .build();
            }
        }


        paymentRequest.paymentRequest(cart);

        return Response.ok().build();
    }
}
