package ordersdemo.hibernate.orders;

import ordersdemo.hibernate.products.Product;

import javax.persistence.*;

@Entity
@Table(name = "orders_detail")
public class OrderDetail {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="order_id")
    private Long order_id;

    @Column(name="customer_id")
    private Long customer_id;

    @Column(name="price")
    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderDetail() {
    }

    public OrderDetail(Order order, Product product, Double price) {
        this.order_id = order.getId();
        this.customer_id = order.getCustomer_id();
        this.product = product;
        this.price = price;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public Long getProduct_id() {
        return product.getId();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }
}
