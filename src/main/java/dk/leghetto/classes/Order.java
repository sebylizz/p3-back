package dk.leghetto.classes;

import java.util.ArrayList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Order {
    ArrayList<Product> items;
    Double coupon;
    Double sum;

    public Order() {
        this.items = new ArrayList<>();
        this.coupon = 0.0;
        this.sum = 0.0;
    }

    public void addProduct(Product product) {
        this.items.add(product);
        this.sum += product.getPrice();
    }

    public ArrayList<Product> getItems() {
        return items;
    }
}