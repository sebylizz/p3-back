package dk.leghetto.classes;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Product;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.model.Price;

public class PaymentRequest {


    public static void main(String[] args) throws StripeException {
        Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";

        Order order = new Order();
        double sum = order.getSum();
        long amountinCents = (long) (sum * 100);

        //We create the Subtotal as a product for stripe
        ProductCreateParams productParams =
                ProductCreateParams.builder()
                        .setName("Subtotal")
                        .setDescription("Subtotal")
                        .build();
        Product product = Product.create(productParams);
        System.out.println("Success! Here is your starter Subtotal-product-id: " + product.getId());

        //We create the price id for the subtotal-product
        PriceCreateParams params =
                PriceCreateParams
                        .builder()
                        .setProduct(product.getId())
                        .setCurrency("dkk")
                        .setUnitAmount(amountinCents)
                        .build();
        Price price = Price.create(params);
        System.out.println("Success! Here is your starter Subtotal-price-id: " + price.getId());


        Stripe.apiKey = "sk_test_51QA8WbCZh5mI9KbJi0PWYz6XKnbRn1slHQYMrlOpAVG13AJV1HT6kQ9ihNFPbr7uzpmLwIfU6TeXs5m4YOeYFr1U00fKvdltAl";

        //Create the payment link for the subtotal.
        PaymentLinkCreateParams params1 =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice(price.getId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .build();

        PaymentLink paymentLink = PaymentLink.create(params1);
        System.out.println("Here is your payment link: " + paymentLink.getUrl());

    }
}

class Order {
    private static Double sum;

    public Order() {
        this.sum = 450.0; // For example, the order sum is 100.0 DKK
    }

    public static Double getSum() {
        return sum;
    }
}