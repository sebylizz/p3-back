package dk.leghetto.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import dk.leghetto.services.CartService;
import dk.leghetto.services.OrderRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentRequest {
    @Inject
    CartService cartService;

    @Inject
    ProductVariantRepository pvr;

    public String generateSessionId(OrderRequest orderRequest) throws Exception {
        Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";
        Cart cart = cartService.cartFromStrings(orderRequest.getProductIds());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("first_name", orderRequest.getDetails().getFirstName());
        metadata.put("last_name", orderRequest.getDetails().getLastName());
        metadata.put("address", orderRequest.getDetails().getAddress());
        metadata.put("postal_code", orderRequest.getDetails().getPostalCode().toString());
        metadata.put("phone_number", orderRequest.getDetails().getPhoneNumber().toString());
        metadata.put("email", orderRequest.getDetails().getEmail());
        metadata.put("user_id", orderRequest.getDetails().getUserId().toString());

        List<SessionCreateParams.LineItem> items = new ArrayList<>();

        for (ProductVariantDTO product : cart.getItems()) {
            SessionCreateParams.LineItem existingItem;
            existingItem = items.stream()
                    .filter(item -> {
                        try {
                            return Price.retrieve(item.getPrice()).getMetadata().get("id")
                                    .equals(product.getId().toString());
                        } catch (StripeException e) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                long newQuantity = existingItem.getQuantity() + 1;
                SessionCreateParams.LineItem updatedItem = SessionCreateParams.LineItem.builder()
                        .setPrice(existingItem.getPrice())
                        .setQuantity(newQuantity)
                        .build();

                items.remove(existingItem);
                items.add(updatedItem);
            } else {
                PriceCreateParams priceParams = PriceCreateParams
                        .builder()
                        .setProductData(
                                PriceCreateParams.ProductData.builder().setName(product.getName()).build())
                        .setCurrency("DKK")
                        .setUnitAmount(product.getPrice())
                        .putMetadata("id", product.getId().toString())
                        .build();
                Price price = Price.create(priceParams);

                SessionCreateParams.LineItem newItem = SessionCreateParams.LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(1L)
                        .build();

                items.add(newItem);
            }
        }

        for (SessionCreateParams.LineItem item : items) {
            ProductVariant found = pvr
                    .findById(Long.parseLong(Price.retrieve(item.getPrice()).getMetadata().get("id")));
            if (item.getQuantity() > found.getQuantity()) {
                throw new Exception("Not enough stock for " + found.getProduct().getName());
            }

        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/failure")
                .addAllLineItem(items)
                .putAllMetadata(metadata)
                .build();

        Session session = Session.create(params);

        return session.getId();
    }
}
