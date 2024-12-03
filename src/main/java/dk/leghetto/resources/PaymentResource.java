package dk.leghetto.resources;

import dk.leghetto.classes.ProductVariantRepository;
import dk.leghetto.classes.PaymentRequest;

import java.util.List;
import java.util.Map;

import com.stripe.model.LineItem;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;

import dk.leghetto.classes.Customer;
import dk.leghetto.classes.CustomerRepository;
import dk.leghetto.classes.OrderDetailsRepository;
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

@Path("/payment")
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
    @Inject
    CustomerRepository cr;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/generatesessionid")
    public Response generateSessionId(OrderRequest orderRequest) {
        Customer user = cr.findByEmail(orderRequest.getDetails().getEmail());
        if (user != null) {
            orderRequest.setUserId(user.getId());
        }
        try {
            String sessionId = paymentRequest.generateSessionId(orderRequest);
            return Response.ok(sessionId).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/confirmorder")
    public Response confirmOrder(String sessionId) {
        Session session = null;
        try {
            session = Session.retrieve(sessionId);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
            if ("true".equals(paymentIntent.getMetadata().get("processed"))) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("This payment has already been processed.")
                        .build();
            }
            if (!paymentIntent.getStatus().equals("succeeded")) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Payment not succeeded").build();
            }

            Map<String, String> details = session.getMetadata();
            Long id = orderDetailsRepository.add(
                    details.get("first_name"),
                    details.get("last_name"),
                    details.get("address"),
                    Integer.parseInt(details.get("postal_code")),
                    Integer.parseInt(details.get("phone_number")),
                    details.get("email"),
                    Long.parseLong(details.get("user_id")));

            List<LineItem> lineItemCollection = session.listLineItems().getData();
            System.out.println("lic: " + lineItemCollection);

            for (LineItem variant : lineItemCollection) {
                Price price = Price.retrieve(variant.getPrice().getId());
                Long amount = price.getUnitAmount();
                Long variantId = Long.parseLong(price.getMetadata().get("id"));
                orderItemsRepository.add(
                        id,
                        variantId,
                        amount);
                ProductVariant pv = pvr.findById(variantId);
                try {
                    Long currentQuantity = pv.getQuantity();
                    if (currentQuantity <= 0) {
                        throw new Exception("No more product left");
                    }
                    if (currentQuantity == 5) {
                        mailService.sendMail("ekkr", "Low stock",
                                "Dette produkt er i low stock: " + pv.getProduct().getName());
                    }
                    if (currentQuantity == 1) {
                        mailService.sendMail("ekkr", "Out of stock",
                                "Dette produkt er udsolgt: " + pv.getProduct().getName());
                    }
                    pv.setQuantity(currentQuantity - 1);
                } catch (Exception e) {
                    return Response.serverError().entity(e).build();
                }
                pv.persist();
            }

            paymentIntent = paymentIntent.update(Map.of("metadata", Map.of("processed", "true")));

            String body = "Hej Lasse, I har fået en ny ordre til: " +
                    details.get("first_name") + ",\n\n"
                    + "Ordre detaljer:\n\n"
                    + "Fornavn: " + details.get("first_name") + "\n"
                    + "Efternavn: " + details.get("last_name") + "\n"
                    + "Adresse: " + details.get("address") + "\n"
                    + "Postnummer: " + details.get("postal_code") + "\n"
                    + "Telefonnummer: " + details.get("phone_number") + "\n"
                    + "Email: " + details.get("email") + "\n\n"
                    + "Ordre indhold:\n\n"
                    + lineItemCollection + "\n\n";

            mailService.sendMail("ekkr", "Ny ordre", body);

            return Response.ok(details).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
