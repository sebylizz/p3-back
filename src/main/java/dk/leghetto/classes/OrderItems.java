package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "order_items")
public class OrderItems extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_details_id")
    private Long orderDetailsId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "price")
    private Long price;

    public OrderItems() {

    }

    public OrderItems(Long orderDetailsId, Long variantId, Long price) {
        this.orderDetailsId = orderDetailsId;
        this.variantId = variantId;
        this.price = price;
    }

    public Long getId() { return id; }
    public Long getOrderDetailsId() { return orderDetailsId; }
    public Long getVariantId() { return variantId; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
}