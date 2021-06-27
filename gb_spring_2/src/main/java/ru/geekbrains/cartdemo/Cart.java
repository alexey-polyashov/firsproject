package ru.geekbrains.cartdemo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class Cart {

    private List<Product> productList = new ArrayList<>();

    public void display() {
        if(productList.isEmpty()){
            System.out.println("Cart is empty");
            System.out.println("--------------- Total cost = 0");
            return;
        }
        System.out.println("Cart contains:");
        System.out.println("№; ID; Name; Cost");
        int count = 1;
        int totalCost = 0;
        for (Product pr: productList) {
            System.out.printf("  %s - %s:   %s: %s", count,  pr.getId(), pr.getTitle(), pr.getCost());
            System.out.println();
            totalCost+=pr.getCost();
        }
        System.out.println("--------------- Total cost = " + totalCost);
    }

    public void clear() {
        productList.clear();
    }

    public void addProduct(Product product) {
        productList.add(product);
    }
}
