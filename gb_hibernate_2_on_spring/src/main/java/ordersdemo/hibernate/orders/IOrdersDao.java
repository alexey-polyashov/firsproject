package ordersdemo.hibernate.orders;

import java.sql.Date;
import java.util.List;

public interface IOrdersDao {

    List<OrderDetail> getDetailsForOrderId(Long id);

    Order getOrderForId(Long id);
    Long newOrder(Date date, Long customerId);
    void addProductInOrder(Long orderId, Long productId, Double price);

}
