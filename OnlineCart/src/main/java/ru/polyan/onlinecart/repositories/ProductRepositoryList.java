package ru.polyan.onlinecart.repositories;

import org.springframework.stereotype.Component;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ProductRepositoryList implements IProductRepository {

    private List<Product> productList;

    @Override
    @PostConstruct
    public void initRepository() {
        productList = new ArrayList<>();
        productList.add(new Product(1, "Potato", 20));
        productList.add(new Product(2, "Carrot", 7));
        productList.add(new Product(3, "Tomato", 50));
        productList.add(new Product(4, "Strawberry", 80));
        productList.add(new Product(5, "Banana", 25));
    }

    @Override
    public Product getProductByID(int prodId) throws ResourceNotFoundException{
        for (Product pr: productList) {
            if(pr.getId() == prodId){
                return pr;
            }
        }
        throw new ResourceNotFoundException();
    }

    @Override
    public boolean addProduct(Product product) {
        return productList.add(product);
    }

    @Override
    public List<Product> productList() {
        return Collections.unmodifiableList(productList);
    }

    @Override
    public boolean removeProduct(Product product) {
        return productList.remove(product);
    }

}

