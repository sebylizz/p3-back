package dk.leghetto.classes;

import jakarta.persistence.*;
import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "product_price")
public class ProductPrice extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long price;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_discount", nullable = false)
    private Boolean isDiscount;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsDiscount() { return isDiscount; }
    public void setIsDiscount(Boolean isDiscount) { this.isDiscount = isDiscount; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
