package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "order_items")
public class OrderItems extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_details_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OrderDetails orderDetails;

    @ManyToOne
    @JoinColumn(name = "variant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductVariant productVariant;

    @Column(name = "order_details_id", insertable = false, updatable = false)
    private Long orderDetailsId;

    @Column(name = "variant_id", insertable = false, updatable = false)
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
    public OrderDetails getOrderDetails() { return orderDetails; }
    public ProductVariant getProductVariant() { return productVariant; }
    public Long getOrderDetailsId() { return orderDetailsId; }
    public Long getVariantId() { return variantId; }
    public Long getPrice() { return price; }

    public void setPrice(Long price) { this.price = price; }
}
