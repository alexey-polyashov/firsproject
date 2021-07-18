package ordersdemo.hibernate.customers;

import ordersdemo.hibernate.orders.Order;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<Order> orders;

    public Customer() {
    }

    public Customer(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ID - " + id + ", NAME - '" + name + "'" +
                "";
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    void addProduct(Order order){
        orders.add(order);
    }

}
