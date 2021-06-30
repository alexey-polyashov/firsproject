package ru.polyan.onlinecart;

import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryList implements IProductRepository<Product>{

    private List<Product> productList = new ArrayList<>();

    @Override
    public void initRepository() {
        productList.add(new Product(1, "Potato", 20));
        productList.add(new Product(2, "Carrot", 7));
        productList.add(new Product(3, "Tomato", 50));
        productList.add(new Product(4, "Strawberry", 80));
        productList.add(new Product(4, "Banana", 25));
    }

    @Override
    public Product getProductByID(int prodId) {
        for (Product pr: productList) {
            if(pr.getId() == prodId){
                return pr;
            }
        }
        return null;
    }

    @Override
    public boolean addProduct(Product product) {
        return productList.add(product);
    }

    @Override
    public List<Product> productList() {
        return productList;
    }

    @Override
    public boolean removeProduct(Product product) {
        return productList.remove(product);
    }



}

