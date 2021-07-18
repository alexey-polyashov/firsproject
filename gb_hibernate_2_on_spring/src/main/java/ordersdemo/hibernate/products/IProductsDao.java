package ordersdemo.hibernate.products;

import ordersdemo.hibernate.customers.Customer;
import ordersdemo.hibernate.orders.OrderDetail;

import java.util.List;
import java.util.Set;

public interface IProductsDao {

    Product findById(Long id);

    List<Product> findAll();

    void deleteById(Long id);

    void saveOrUpdate(Product prod);

    Set<Customer> getCustomersForProductId(Long productId);

    List<OrderDetail> getOrdersDetails(Long productId);
}
