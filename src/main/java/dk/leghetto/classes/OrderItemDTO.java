package dk.leghetto.classes;

public class OrderItemDTO {
    private String productName;
    private String size;
    private String color;
    private Double price;

    public OrderItemDTO(String productName, String size, String color, Double price) {
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

