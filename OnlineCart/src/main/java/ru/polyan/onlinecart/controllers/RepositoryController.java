package ru.polyan.onlinecart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.polyan.onlinecart.repositories.IProductRepository;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.services.ProductService;
import ru.polyan.onlinecart.utils.ResourceNotFoundException;
import ru.polyan.onlinecart.utils.ServiceResponse;

import java.util.List;

@Controller
public class RepositoryController {

    ProductService productService;

    @Autowired
    void RepositoryController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping(value = "/repository")
    public String repository(Model model){
        List<Product> prodList = productService.productList();
        if(!model.containsAttribute("errors")){
            model.addAttribute("errors", new String[0]);
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
    public String addPosition(Model model, RedirectAttributes redirAttr, @RequestParam String title, @RequestParam String cost){

        ServiceResponse sr = productService.addProduct(title, cost);
        if(sr.isSuccess()){
            redirAttr.addFlashAttribute("message", "Product is added to list.");
        }else{
            redirAttr.addFlashAttribute("errors", sr.getErrors());
        }

        return "redirect:../repository";

    }
}
