package ordersdemo.hibernate.products;

import ordersdemo.hibernate.customers.Customer;
import ordersdemo.hibernate.orders.OrderDetail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ProductsDao implements IProductsDao {

    private SessionFactory sf;

    public void setFactory(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Product findById(Long id){
        try (Session session = sf.getCurrentSession()) {
            session.getTransaction().begin();
            Product prod = session.find(Product.class, id);
            session.getTransaction().commit();
            return prod;
        }
    }

    @Override
    public List<Product> findAll(){
        try (Session session = sf.getCurrentSession()) {
            List<Product> listProd;
            session.getTransaction().begin();
            listProd = session.createQuery("SELECT a FROM Product a", Product.class).getResultList();
            session.getTransaction().commit();
            return listProd;
        }
    }

    @Override
    public void deleteById(Long id){
        try (Session session = sf.getCurrentSession()) {
            session.getTransaction().begin();
            Product prod = session.find(Product.class, id);
            session.remove(prod);
            session.getTransaction().commit();
        }
    }

    @Override
    public void saveOrUpdate(Product prod){
        try (Session session = sf.getCurrentSession()) {
            session.getTransaction().begin();
            session.saveOrUpdate(prod);
            session.getTransaction().commit();
        }
    }

    @Override
    public Set<Customer> getCustomersForProductId(Long productId) {
        try (Session session = sf.getCurrentSession()) {
            session.getTransaction().begin();
            Product prod = session.find(Product.class, productId);
            Set<Customer> customers = prod.getCutomers();
            for (Customer c: customers) {
            }
            session.getTransaction().commit();
            return customers;
        }
    }

    @Override
    public List<OrderDetail> getOrdersDetails(Long productId) {
        try (Session session = sf.getCurrentSession()) {
            session.getTransaction().begin();
            Product prod = session.find(Product.class, productId);
            List<OrderDetail> orderDetails = prod.getOrdersDetails();
            for (OrderDetail c: orderDetails) {
            }
            session.getTransaction().commit();
            return orderDetails;
        }
    }

}
