package ru.polyan.onlinecart;

import java.util.List;

public interface IProductRepository<T extends Product> {
    void initRepository();
    boolean addProduct(T product);
    List<T> productList();
    boolean removeProduct(T product);
    T getProductByID(int prodId);
}
