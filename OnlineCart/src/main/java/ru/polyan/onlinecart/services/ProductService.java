package ru.polyan.onlinecart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.repositories.IProductRepository;
import ru.polyan.onlinecart.repositories.ProductRepositoryList;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;

import java.util.List;

@Service
public class ProductService {

    private IProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepositoryList productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductByID(int id) throws ResourceNotFoundException {
        return productRepository.getProductByID(id);
    }

    public List<Product> productList() {
        return productRepository.productList();
    }

    public boolean addProduct(Product product)  {
        return productRepository.addProduct(product) ;
    }
}
