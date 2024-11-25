package dk.leghetto.classes;

import java.util.ArrayList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Order {
    ArrayList<ProductVariantDTO> items;
    Double coupon;
    Double sum;

    public Order() {
        this.items = new ArrayList<>();
        this.coupon = 0.0;
        this.sum = 0.0;
    }

    public void addProduct(ProductVariantDTO product) {
        this.items.add(product);
        this.sum += 69;
    }

    public ArrayList<ProductVariantDTO> getItems() {
        return items;
    }
}
