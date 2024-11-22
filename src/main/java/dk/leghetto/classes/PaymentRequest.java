package dk.leghetto.classes;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PaymentLinkCreateParams.LineItem;
import com.stripe.param.PriceCreateParams;
import com.stripe.model.Price;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;

@ApplicationScoped
public class PaymentRequest {
    public String paymentRequest(Order order) throws StripeException {
        Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";

        ArrayList<LineItem> items = new ArrayList<>();
        for (ProductVariantDTO product : order.getItems()) {

            PriceCreateParams priceParams =
                    PriceCreateParams
                            .builder()
                            .setProductData(
                                    PriceCreateParams.ProductData.builder().setName(product.getName()).build()
                            )
                            .setCurrency("DKK")
                            .setUnitAmount(product.getPrice())
                            .build();
            Price price = Price.create(priceParams);

            Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";

            LineItem i = LineItem.builder()
                        .setPrice(price.getId())
                        .setQuantity(1L)
                        .build();

            items.add(i);
        }

        PaymentLinkCreateParams paymentParams =
                PaymentLinkCreateParams.builder()
                        .addAllLineItem(items)
                        .build();

        PaymentLink paymentLink = PaymentLink.create(paymentParams);

        return paymentLink.getUrl();
    }
}
