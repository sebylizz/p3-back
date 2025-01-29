package dk.leghetto.classes;

public class OrderSummaryDTO {
    private Long orderId;
    private String productNames;
    private double totalPrice;

    public OrderSummaryDTO(Long orderId, String productNames, double totalPrice) {
        this.orderId = orderId;
        this.productNames = productNames;
        this.totalPrice = totalPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

