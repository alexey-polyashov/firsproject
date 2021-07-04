package ru.polyan.onlinecart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.repositories.IProductRepository;
import ru.polyan.onlinecart.repositories.ProductRepositoryList;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;
import ru.polyan.onlinecart.utils.ServiceResponse;

import java.util.ArrayList;
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

    public ServiceResponse addProduct(String title, String cost)  {

        boolean success = true;
        float parseCost = 0f;

        List<String> errors = new ArrayList<String>();

        try {
            parseCost = Float.parseFloat(cost);
        }catch (NumberFormatException e){
            errors.add("COST is not number");
            success = false;
        }

        if(title.isEmpty()){
            errors.add("TITLE is empty");
            success = false;
        }

        if(success){
            productRepository.addProduct(title, parseCost);
        }

        return new ServiceResponse(success, errors.toArray(new String[0]));
    }
}
