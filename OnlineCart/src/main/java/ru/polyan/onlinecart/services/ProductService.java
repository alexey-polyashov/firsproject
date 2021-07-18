package ru.polyan.onlinecart.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.repositories.ProductRepositoryList;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;
import ru.polyan.onlinecart.utils.ServiceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositoryList productRepository;

    public List<Product> productList() {
        return productRepository.findAll();
    }

    public Product getProductByID(Long id) throws ResourceNotFoundException {
        Optional<Product> prod = productRepository.findById(id);
        if(prod.isEmpty()){
            throw new ResourceNotFoundException("Exception: id=" + id + " not found.");
        }
        return productRepository.findById(id).get();
    }

    public ServiceResponse addProduct(String title, String price)  {

        boolean success = true;
        double parsePrice = 0;

        List<String> errors = new ArrayList<>();

        try {
            parsePrice = Double.parseDouble(price);
        }catch (NumberFormatException e){
            errors.add("PRICE is not number");
            success = false;
        }

        if(title.isEmpty()){
            errors.add("TITLE is empty");
            success = false;
        }

        if(success){
            Product product = new Product();
            product.setTitle(title);
            product.setPrice(parsePrice);
            productRepository.save(product);
        }

        return new ServiceResponse(success, errors.toArray(new String[0]));
    }

    public void deleteProduct(Long id) throws ResourceNotFoundException{
        productRepository.deleteById(id);
    }

    public List<Product> productList_FilterOnPrice(int minprice, int maxprice) {

        if(maxprice>0){
            //return productRepository.findAll();
            return productRepository.findByPriceBetween(minprice,maxprice);
        }else{
            //return productRepository.findAll();
            return productRepository.findByPriceGreaterThanEqual(minprice);
        }

    }
}
