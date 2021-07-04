package ru.polyan.onlinecart.repositories;

import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;

import java.util.List;

public interface IProductRepository {
    void initRepository();
    boolean addProduct(Product product);
    List<Product> productList();
    boolean removeProduct(Product product);
    Product getProductByID(int prodId) throws ResourceNotFoundException;
}
