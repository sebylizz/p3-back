package dk.leghetto.classes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

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

    public OrderDetails() {
    }

    public OrderDetails create(String firstName, String lastName, String address, Integer postalCode,
            Integer phoneNumber, String email, Long userId) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.firstName = firstName;
        orderDetails.lastName = lastName;
        orderDetails.address = address;
        orderDetails.postalCode = postalCode;
        orderDetails.phoneNumber = phoneNumber;
        orderDetails.email = email;
        orderDetails.userId = userId;

        return orderDetails;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
