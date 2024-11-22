package dk.leghetto.classes;

public class ProductVariantDTO {
    private Long id;
    private Long price;
    private String name;

    public ProductVariantDTO () {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
