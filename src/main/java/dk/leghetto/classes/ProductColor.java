package dk.leghetto.classes;
import jakarta.persistence.*;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "product_color")
public class ProductColor extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Colors color;

    @Column(name = "main_image")
    private String mainImage;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Column(name = "total_sales", nullable = false)
    private Integer totalSales = 0;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Products getProduct() { return product; }
    public void setProduct(Products product) { this.product = product; }

    public Colors getColor() { return color; }
    public void setColor(Colors color) { this.color = color; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
    
}
