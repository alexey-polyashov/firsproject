package ordersdemo.hibernate.orders;

import ordersdemo.hibernate.products.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

@Component
public class OrdersDao implements IOrdersDao{

    private SessionFactory sf;

    public void setFactory(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public List<OrderDetail> getDetailsForOrderId(Long id) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Order order = session.get(Order.class, id);
            List<OrderDetail> products = order.getDetails();
            for (OrderDetail od: products) {
            }
            session.getTransaction().commit();
            return products;
        }
    }

    @Override
    public Order getOrderForId(Long id) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Order order = session.get(Order.class, id);
            session.getTransaction().commit();
            return order;
        }
    }

    @Override
    public Long newOrder(Date date, Long customerId) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Order order = new Order(date, customerId);
            session.save(order);
            session.getTransaction().commit();
            return order.getId();
        }
    }

    @Override
    public void addProductInOrder(Long orderId, Long productId, Double price) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            Product product = session.get(Product.class, productId);
            order.addProduct(product, price);
            session.getTransaction().commit();
        }
    }

}
