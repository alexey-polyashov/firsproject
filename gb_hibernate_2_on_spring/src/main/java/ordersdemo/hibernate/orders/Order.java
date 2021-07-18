package ordersdemo.hibernate.orders;

import ordersdemo.hibernate.products.Product;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="order_date")
    private Date order_date;

    @Column(name="customer_id")
    private Long customer_id;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderDetail> orderDetail;

    public Order() {
    }

    public List<OrderDetail> getDetails() {
        return Collections.unmodifiableList(orderDetail);
    }

    void addProduct(Product product, Double price){
        orderDetail.add(new OrderDetail(this, product, price));
    }

    public Order(Date date, Long customer_id) {
        this.order_date = date;
        this.customer_id = customer_id;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return order_date;
    }

    public void setDate(Date date) {
        this.order_date = date;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + order_date +
                ", customer_id=" + customer_id +
                '}';
    }
}
