package ru.polyan.onlinecart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.polyan.onlinecart.IProductRepository;
import ru.polyan.onlinecart.Product;
import ru.polyan.onlinecart.ProductRepositoryList;

import java.util.List;

@Controller
public class RepositoryController {

    IProductRepository<Product> productRepository = new ProductRepositoryList();


    @Autowired
    void init(){
        productRepository.initRepository();
    }

    @GetMapping(value = "/repository")
    public String repository(Model model){
        List<Product> prodList = productRepository.productList();
        if(!model.containsAttribute("errors")){
            model.addAttribute("errors", "");
        }
        if(!model.containsAttribute("message")){
            model.addAttribute("message", "");
        }
        model.addAttribute("products", prodList);
        model.addAttribute("total_inpage", prodList.size());
        model.addAttribute("newproduct", new Product());
        return "repository";
    }

    @PostMapping(value = "/repository/addposition")
    public String addPosition(Model model, RedirectAttributes redirAttr, @RequestParam String id, @RequestParam String title, @RequestParam String cost){

        int parseId =0;
        float parseCost = 0f;
        String error = "";

        try {
            parseId = Integer.parseInt(id);
            if(productRepository.getProductByID(parseId) != null){
                error += "Product with ID:" + parseId + " already exists; ";
            }
        }catch (NumberFormatException e){
            error += "ID is not number; ";
        }
        try {
            parseCost = Float.parseFloat(cost);
        }catch (NumberFormatException e){
            error += "COST is not number; ";
        }

        if(title.isEmpty()){
            error += "TITLE is empty; ";
        }

        if(!error.isEmpty()){
            error = "ERRORS: " + error;
            redirAttr.addFlashAttribute("errors", error);
        }else {
            redirAttr.addFlashAttribute("message", "Product is added to list.");
            productRepository.addProduct(new Product(parseId, title, parseCost));
        }

        return "redirect:../repository";

    }
}
