package ru.geekbrains.cartdemo;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductRepository implements IProductRepository {

    private final Map<Integer, Product> products;

    @Override
    public boolean contains(int id) {
        return products.containsKey(id);
    }

    @Override
    public Product getProduct(int id) {
        return products.get(id);
    }

    public ProductRepository() {
        this.products = new HashMap<Integer, Product>(){{
                put(1, new Product(1, "Carrot", 3f));
                put(2, new Product(2, "Potato", 5f));
                put(3, new Product(3, "Mango", 20f));
                put(4, new Product(4, "Apple", 8f));
                put(5, new Product(5, "Chery", 10f));
                put(6, new Product(6, "Banan", 7f));
                put(7, new Product(7, "Strawberry", 11f));
                put(8, new Product(8, "Orange", 12f));
                put(9, new Product(9, "Parsley", 5f));
                put(10, new Product(10, "Pineapple", 25f));
            }};
    }

    @Override
    public void listAllProducts() {
        if(products.isEmpty()){
            System.out.println("Product list is empty");
        }
        System.out.println("Product list:");
        System.out.println("ID; Name; Cost");
        for (Map.Entry<Integer, Product> productEntry: products.entrySet()) {
            Product pr = productEntry.getValue();
            System.out.printf("  %s:   %s: %s", pr.getId(), pr.getTitle(), pr.getCost());
            System.out.println();
        }
        System.out.println("---------------");
    }
}
