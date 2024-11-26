package dk.leghetto.services;

import dk.leghetto.classes.Cart;
import dk.leghetto.classes.OrderDetails;

public class OrderRequest {
    private OrderDetails details;
    private String productIds;

    public OrderDetails getDetails() { return details; }
    public String getProductIds() { return productIds; }
}
