package dk.leghetto.classes;

import java.util.ArrayList;
import java.util.List;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentRequest {
    public String generateSessionId (Cart order) throws StripeException {
        Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";

        List<SessionCreateParams.LineItem> items = new ArrayList<>();
        for (ProductVariantDTO product : order.getItems()) {

            PriceCreateParams priceParams = PriceCreateParams
                    .builder()
                    .setProductData(
                            PriceCreateParams.ProductData.builder().setName(product.getName()).build())
                    .setCurrency("DKK")
                    .setUnitAmount(product.getPrice())
                    .build();
            Price price = Price.create(priceParams);

            SessionCreateParams.LineItem i = SessionCreateParams.LineItem.builder()
                    .setPrice(price.getId())
                    .setQuantity(1L)
                    .build();

            items.add(i);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/failure")
                .addAllLineItem(items)
                .build();

        Session session = Session.create(params);

        return session.getId();
    }
}
