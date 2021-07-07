package products;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductDao implements IProductDao{

    @Override
    public Product findById(EntityManager em, Long id){
        em.getTransaction().begin();
        Product prod = em.find(Product.class, id);
        em.getTransaction().commit();
        return prod;
    }

    @Override
    public List<Product> findAll(EntityManager em){
        List<Product> listProd;
        em.getTransaction().begin();
        listProd =  em.createQuery("SELECT a FROM Product a", Product.class).getResultList();
        em.getTransaction().commit();
        return listProd;
    }

    @Override
    public void deleteById(EntityManager em, Long id){
        em.getTransaction().begin();
        Product prod = em.find(Product.class, id);
        em.remove(prod);
        em.getTransaction().commit();
    }

    @Override
    public void saveOrUpdate(EntityManager em, Product prod){
        em.getTransaction().begin();
        em.persist(prod);
        em.getTransaction().commit();
    }

}
