package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Table(name = "order_details")
public class OrderDetails extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code")
    private Integer postalCode;

    @Column(name = "phone_number")
    private Integer phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "orderDetails") 
    private List<OrderItems> orderItems;

    public OrderDetails() {}

        // Constructor to initialize fields
        public OrderDetails(String firstName, String lastName, String address, Integer postalCode, Integer phoneNumber, String email, Long userId) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.postalCode = postalCode;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.userId = userId;
        }
    
        // Factory method to create an instance
        public static OrderDetails create(String firstName, String lastName, String address, Integer postalCode, Integer phoneNumber, String email, Long userId) {
            return new OrderDetails(firstName, lastName, address, postalCode, phoneNumber, email, userId);
        }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public void setUserId(Long userId) {

        this.userId = userId;

    }
}
