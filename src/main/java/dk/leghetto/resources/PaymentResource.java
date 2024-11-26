package dk.leghetto.resources;

import com.stripe.exception.StripeException;
import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.PaymentRequest;
import dk.leghetto.classes.Order;
import dk.leghetto.classes.ProductVariantDTO;
import dk.leghetto.classes.OrderDetailsRepository;
import dk.leghetto.services.MailService;
import dk.leghetto.services.OrderRequest;
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
    PaymentRequest paymentRequest;

    @Inject
    OrderDetailsRepository orderDetailsRepository;
    @Inject
    MailService mailService;

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
                }
            else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product variant not found.")
                        .build();
            }
        }

        String paymentLink = paymentRequest.paymentRequest(order);

        return Response.ok(paymentLink).build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addOrder")
    public Response addOrder(OrderRequest orderRequest) {

        orderDetailsRepository.add(
                orderRequest.getFirstName(),
                orderRequest.getLastName(),
                orderRequest.getAddress(),
                orderRequest.getPostalCode(),
                orderRequest.getPhoneNumber(),
                orderRequest.getEmail()
        );

        String body = "Hallo bøsser i har fået en ny ordre til " + orderRequest.getFirstName() + ",\n\n"
                + "Ordre detaljer:\n\n"
                + "First name: " + orderRequest.getFirstName() + "\n"
                + "Last name: " + orderRequest.getLastName() + "\n"
                + "Adresse: " + orderRequest.getAddress() + "\n"
                + "Post nummer: " + orderRequest.getPostalCode() + "\n"
                + "Telefonnummer: " + orderRequest.getPhoneNumber() + "\n"
                + "Email: " + orderRequest.getEmail() + "\n\n"
                + "Send den afsted faggets!";

        mailService.sendMail("emil624g@gmail.com", "New order", body);

        return Response.ok("Order created").build();
    }
}
