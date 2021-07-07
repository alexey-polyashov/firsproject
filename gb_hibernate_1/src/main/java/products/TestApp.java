package products;

import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;


public class TestApp {

    public static void main(String[] args) {
        EntityManagerFactory factory = new Configuration()
                .configure("configs/products/hibernate.cfg.xml")
                .buildSessionFactory();
        EntityManager em = factory.createEntityManager();

        Product prod;

        ProductDao pd = new ProductDao();

        System.out.println("Find by id = 1");
        prod = pd.findById(em, 1l);
        prod.display();
        System.out.println();

        System.out.println("Find all");
        List<Product> prods = pd.findAll(em);
        for (Product element: prods) {
            element.display();
        }
        System.out.println();

        System.out.println("Save 'new prod 10'");
        pd.saveOrUpdate(em, new Product("new prod 10", 5.6f));

        prods = pd.findAll(em);
        for (Product element: prods) {
            element.display();
        }

    }

}
