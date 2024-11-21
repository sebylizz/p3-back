package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import dk.leghetto.classes.Product;
import dk.leghetto.classes.ProductVariant;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.ProductRepository;
import dk.leghetto.classes.Order;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/productpayment")
public class PaymentResource {
    @Inject
    ProductVariantRepository productVariantRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    PaymentRequest paymentRequest;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/carttest")
    public Response cart(List<Long> variantIds) throws StripeException {

        Order order = new Order();

        for (Long variantId : variantIds) {
            ProductVariant productVariant = productVariantRepository.findById(variantId);
            if (productVariant != null) {
                Product product = productRepository.findById(productVariant.getProductId());
                if (product != null) {
                    order.addProduct(product);
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Product not found.")
                            .build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product variant not found.")
                        .build();
            }
        }

        String paymentLink = paymentRequest.paymentRequest(order);

        return Response.ok(paymentLink).build();
    }
}
