package ordersdemo.hibernate.products;

import ordersdemo.hibernate.customers.Customer;
import ordersdemo.hibernate.orders.OrderDetail;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="price")
    private Double price;

    @OneToMany
    @JoinTable(
            name = "orders_detail",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<Customer> cutomers;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Product(String title, Double price) {
        this.title = title;
        this.price = price;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                '}';
    }

    public Set<Customer> getCutomers() {
        return Collections.unmodifiableSet(cutomers);
    }

    public List<OrderDetail> getOrdersDetails() {
        return Collections.unmodifiableList(orderDetails);
    }
}
