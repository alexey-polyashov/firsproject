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
        productList.add(new Product(0, "Potato", 20));
        productList.add(new Product(1, "Carrot", 7));
        productList.add(new Product(2, "Tomato", 50));
        productList.add(new Product(3, "Strawberry", 80));
        productList.add(new Product(4, "Banana", 25));
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
    public boolean addProduct(String title, float cost) {
        Product prod = new Product( productList.size()+1, title, cost);
        return productList.add(prod);
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

