package products;

import javax.persistence.EntityManager;
import java.util.List;

public interface IProductDao {
    Product findById(EntityManager em, Long id);

    List<Product> findAll(EntityManager em);

    void deleteById(EntityManager em, Long id);

    void saveOrUpdate(EntityManager em, Product prod);
}
