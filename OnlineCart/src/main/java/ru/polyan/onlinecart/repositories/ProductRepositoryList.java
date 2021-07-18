package ru.polyan.onlinecart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public interface ProductRepositoryList  extends JpaRepository<Product, Long> {

    List<Product> findByPriceBetween(double minprice, double maxprice);
    List<Product> findByPriceGreaterThanEqual(double minprice);

}

