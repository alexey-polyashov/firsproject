package ordersdemo;

import ordersdemo.hibernate.customers.Customer;
import ordersdemo.hibernate.orders.Order;
import ordersdemo.hibernate.orders.OrderDetail;
import ordersdemo.hibernate.products.Product;
import ordersdemo.hibernate.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class OrdersDemo {

    @Autowired
    private static CustomersService customersService;

    public static void main(String[] args) {

        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);

        Scanner scan = new Scanner(System.in);

        customersService = appContext.getBean("customersService", CustomersService.class);

        while(true) {
            System.out.println("Enter action:\n 1-list customers,\n 2-list customer orders,\n 3-list products list,\n 4-list customers for product\n 5-exit");
            if(scan.hasNext()){
                int chois = scan.nextInt();
                if(chois==5){
                    break;
                }else if(chois==1){
                    System.out.println("Cusomers:");
                    customersService.showCustomers();
                    System.out.println();
                }else if(chois==2){
                    System.out.println("  enter customer ID:");
                    long id = 0;
                    while(id == 0){
                        if(scan.hasNext()){
                            id = scan.nextInt();
                            if(id<0){
                                System.out.println("    Enter correct number.");
                                id = 0;
                            }else{
                                List<Order> orders = customersService.getOrdersForCustomer(id);
                                System.out.println("Orders for customer id=" + id + ":");
                                for (Order order:orders) {
                                    System.out.printf("    Order %d from %s \n", order.getId(), order.getDate().toString());
                                    for (OrderDetail detail: order.getDetails()) {
                                        System.out.printf("     TITLE - %s, PRICE - %s \n", detail.getProduct().getTitle(), detail.getPrice());
                                    }
                                }
                                System.out.println();
                            }
                        }
                    }
                }else if(chois==3){
                    List<Product> prodList = customersService.getAllProducts();
                    System.out.println("Products:");
                    for (Product prod: prodList) {
                        System.out.printf(" ID - %s:    TITLE - %s, PRICE - %s\n", prod.getId(), prod.getTitle(), prod.getPrice());
                    }
                    System.out.println();
                }else if(chois==4){
                    System.out.println("  enter product ID:");
                    long id = 0;
                    while(id == 0){
                        if(scan.hasNext()){
                            id = scan.nextInt();
                            if(id<0){
                                System.out.println("    Enter correct number.");
                                id = 0;
                            }else{
                                Set<Customer> cusmerList = customersService.getCustomersForProduct(id);
                                System.out.println("Customers for product id=" + id + ":");
                                for (Customer customer:cusmerList) {
                                    System.out.printf("    Customer %d, name %s \n", customer.getId(), customer.getName());
                                }
                                System.out.println();
                            }
                        }
                    }
                }else{
                    System.out.println("Unexpected symbol!");
                }
            }
        }

        scan.close();
        customersService.shutdown();
        appContext.close();

    }

}
