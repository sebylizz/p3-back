package dk.leghetto.classes;

public class Receipt {

    OrderDetails details;
    Order cart;

    public Receipt() {

    }

    public Receipt(OrderDetails details, Order cart) {
        this.details = details;
        this.cart = cart;
    }

}
