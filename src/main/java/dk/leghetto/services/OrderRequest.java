package dk.leghetto.services;

import dk.leghetto.classes.Cart;
import dk.leghetto.classes.OrderDetails;

public class OrderRequest {
    private OrderDetails details;
    private Cart cart;

    public OrderDetails getDetails() { return details; }
    public Cart getCart() { return cart; }
}
