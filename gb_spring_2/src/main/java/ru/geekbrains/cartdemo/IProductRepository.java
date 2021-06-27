package ru.geekbrains.cartdemo;

import org.springframework.stereotype.Component;

@Component
public interface IProductRepository {
    boolean contains(int id);

    Product getProduct(int id);

    void listAllProducts();
}
