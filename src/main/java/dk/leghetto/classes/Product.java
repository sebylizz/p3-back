package dk.leghetto.classes;

import jakarta.persistence.*;

@Entity
@Table(name = "products", schema = "dev")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private String size;

    @Column(name = "price")
    private Double price;


    @Column(name = "image")
    private String image;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "main_image")
    private String mainImage;

    public Product() {
    }


    public Product(String name, String size, Double price, int quantity, String image, String mainImage) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.quantity=quantity;
        this.image=image;
        this.mainImage=mainImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

}
