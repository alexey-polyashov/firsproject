package ru.geekbrains.cartdemo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class CartDemo {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);

        Scanner scan = new Scanner(System.in);

        Cart cart = appContext.getBean(Cart.class);
        ProductRepository productRepository = appContext.getBean("productRepository", ProductRepository.class);


        while(true) {
            System.out.println("Enter action:\n 1-list products,\n 2-add to cart,\n 3-list cart contains,\n 4-clear cart,\n 5-new cart,\n 6-exit");
            if(scan.hasNext()){
                int chois = scan.nextInt();
                if(chois==6){
                    break;
                }else if(chois==1){
                    productRepository.listAllProducts();
                }else if(chois==2){
                    System.out.println("  enter product number:");
                    int id = 0;
                    while(id == 0){
                        if(scan.hasNext()){
                            id = scan.nextInt();
                            if(id<0 || !productRepository.contains(id)){
                                System.out.println("    Product with such number is not exist.");
                                id=0;
                            }
                        }
                    }
                    Product product = productRepository.getProduct(id);
                    cart.addProduct(product);
                    System.out.println("Product is added");
                }else if(chois==3){
                    cart.display();
                }else if(chois==4){
                    cart.clear();
                }else if(chois==5){
                    cart = appContext.getBean(Cart.class);
                }else{
                    System.out.println("Unexpected symbol!");
                }
            }
        }

        scan.close();

    }

}
