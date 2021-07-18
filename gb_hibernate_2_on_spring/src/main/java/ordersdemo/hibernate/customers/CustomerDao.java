package ordersdemo.hibernate.customers;

import ordersdemo.hibernate.orders.Order;
import ordersdemo.hibernate.orders.OrderDetail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerDao implements ICustomerDoa{

    private SessionFactory sf;

    public void setFactory(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public List<Order> getOrdersForCustomerId(Long id) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, id);
            List<Order> orders = customer.getOrders();
            for (Order order: orders) {
                List<OrderDetail> details = order.getDetails();
                for (OrderDetail detail: details) {
                }
            }
            session.getTransaction().commit();
            return orders;
        }
    }

    @Override
    public List<Customer> getCustomers() {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            List<Customer> customers = session.createQuery("from Customer", Customer.class).getResultList();
            session.getTransaction().commit();
            return customers;
        }
    }

    @Override
    public Customer getCustomerForId(Long id) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, id);
            session.getTransaction().commit();
            return customer;
        }
    }

    @Override
    public Long newCustomer(String name) {
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            Customer customer = new Customer(name);
            session.save(customer);
            session.getTransaction().commit();
            return customer.getId();
        }
    }

}
