package ru.polyan.onlinecart.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.polyan.onlinecart.model.Product;
import ru.polyan.onlinecart.services.ProductService;
import ru.polyan.onlinecart.utils.ServiceResponse;
import ru.polyan.onlinecart.utils.ApiResponse;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RepositoryController {

    private final ProductService productService;

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
    public String addPosition(Model model, RedirectAttributes redirAttr, @RequestParam String title, @RequestParam String price){

        ServiceResponse sr = productService.addProduct(title, price);
        if(sr.isSuccess()){
            redirAttr.addFlashAttribute("message", "Product is added to list.");
        }else{
            redirAttr.addFlashAttribute("errors", sr.getErrors());
        }

        return "redirect:../repository";

    }

    //---------------------RESTAPI----------------------------//

    @GetMapping(value = "/repository_api/product/find")
    @ResponseBody
    public List<Product> findProducts(@RequestParam(required = false, defaultValue = "-1") int minprice,
                                      @RequestParam(required = false, defaultValue = "-1") int maxprice){
        if(minprice==-1 && maxprice==-1){
            return productService.productList();
        }else{
            return productService.productList_FilterOnPrice(minprice, maxprice);
        }
    }

    @GetMapping(value = "/repository_api/product/find/{id}")
    @ResponseBody
    public Object findProductById(@PathVariable Long id){
        ApiResponse r;
        try{
            r = ApiResponse.builder()
                    .success(true)
                    .response(productService.getProductByID(id))
                    .build();
        }catch (Exception e){
            log.error(e.getMessage());
            r = ApiResponse.builder()
                    .success(false)
                    .errors(new String[]{e.getMessage()})
                    .build();;
        }
        return r;
    }

    @GetMapping(value = "/repository_api/product/delete/{id}")
    @ResponseBody
    public Object deleteProductById(@PathVariable Long id){
        ApiResponse r;
        try{
            productService.deleteProduct(id);
            r = ApiResponse.builder()
                    .success(true)
                    .response("OK")
                    .build();
        }catch (Exception e){
            log.error(e.getMessage());
            r = ApiResponse.builder()
                    .success(false)
                    .errors(new String[]{e.getMessage()})
                    .build();;
        }
        return r;
    }


    @PostMapping(value = "/repository_api/product/add")
    @ResponseBody
    public Object addProduct(@RequestParam String title, @RequestParam String price){
        ApiResponse r;
        try{
            ServiceResponse sr = productService.addProduct(title, price);
            if(sr.isSuccess()) {
                r = ApiResponse.builder()
                        .success(true)
                        .response("OK")
                        .build();
            }else{
                log.debug(String.join("; ", sr.getErrors()));
                r = ApiResponse.builder()
                        .success(false)
                        .errors(sr.getErrors())
                        .build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            r = ApiResponse.builder()
                    .success(false)
                    .errors(new String[]{e.getMessage()})
                    .build();
        }
        return r;
    }

}
