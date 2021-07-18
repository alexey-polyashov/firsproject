package ordersdemo.hibernate.customers;

import ordersdemo.hibernate.orders.Order;

import java.util.List;

public interface ICustomerDoa {

    List<Order> getOrdersForCustomerId(Long id);
    Customer getCustomerForId(Long id);
    Long newCustomer(String name);
    List<Customer> getCustomers();

}
