package dk.leghetto.classes;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "product_variant")
public class ProductVariant extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Colors color;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Sizes size;

    @Column(nullable = false)
    private Integer quantity;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Products getProduct() { return product; }
    public void setProduct(Products product) { this.product = product; }

    public Colors getColor() { return color; }
    public void setColor(Colors color) { this.color = color; }

    public Sizes getSize() { return size; }
    public void setSize(Sizes size) { this.size = size; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
