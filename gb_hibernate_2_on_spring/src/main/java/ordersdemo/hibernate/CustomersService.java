package ordersdemo.hibernate;

import ordersdemo.hibernate.customers.CustomerDao;
import ordersdemo.hibernate.customers.Customer;
import ordersdemo.hibernate.orders.Order;
import ordersdemo.hibernate.orders.OrderDetail;
import ordersdemo.hibernate.orders.OrdersDao;
import ordersdemo.hibernate.products.Product;
import ordersdemo.hibernate.products.ProductsDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomersService implements Closeable {

    private SessionFactory factory;

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private ProductsDao productsDao;

    @PostConstruct
    public void init() {
        factory = new Configuration()
                .configure("config/hibernate.cfg.xml")
                .buildSessionFactory();

        customerDao.setFactory(factory);
        ordersDao.setFactory(factory);
        productsDao.setFactory(factory);

    }

//    public static void main(String[] args) {
//        try (CustomersService cs = new CustomersService()){
//            cs.init();
//            cs.showCustomers();
//            cs.showOrdersForCustomer(1L);
//            cs.showCustomersForProduct(2L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void showCustomers(){
        List<Customer> customerList = getCustomers();
        for (Customer c: customerList) {
            System.out.println(c);
        }
    }

    public void showOrdersForCustomer(Long cutomerId){
        List<Order> orderList = getOrdersForCustomer(cutomerId);
        for (Order order: orderList) {
            System.out.println("    Order: " + order.getDate());
            List<OrderDetail> products = order.getDetails();
            for (OrderDetail detail : products) {
                System.out.println("        " + detail.getProduct().getTitle() + " - " + detail.getPrice());
            }
        }
    }

    public void showCustomersForProduct(Long productId){
        Set<Customer> customerList = getCustomersForProduct(productId);
        for (Customer customer: customerList) {
            System.out.println("    Customer: " + customer);
        }
    }

    public List<Customer> getCustomers(){
        List<Customer> customerList = customerDao.getCustomers();
        return customerList;
    }

    public List<Order> getOrdersForCustomer(Long cutomerId){
        List<Order> orderList = new ArrayList<>();
        Customer customer = customerDao.getCustomerForId(cutomerId);
        if(customer!=null && customer.getId()!=0){
            orderList = customerDao.getOrdersForCustomerId(customer.getId());
        }
        return orderList;
    }

    public Set<Customer> getCustomersForProduct(Long productId){
        Product product = productsDao.findById(productId);
        Set<Customer> customerList = new HashSet<>();
        if(product!=null){
            customerList = productsDao.getCustomersForProductId(product.getId());
        }
        return customerList;
    }

    public List<Product> getAllProducts(){
        return productsDao.findAll();
    }

    public void shutdown() {
        if(factory!=null) {
            factory.close();
        }
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }
}
