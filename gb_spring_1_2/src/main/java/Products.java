import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Products", urlPatterns = "/products")
public class Products extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(Products.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("start GET method");

        List<Product> prodList = new ArrayList<Product>(){{
            add(new Product(1,"Carrot", 3f));
            add(new Product(2,"Potato", 5f));
            add(new Product(3,"Mango", 20f));
            add(new Product(4,"Apple", 8f));
            add(new Product(5,"Chery", 10f));
            add(new Product(6,"Banan", 7f));
            add(new Product(7,"Strawberry", 11f));
            add(new Product(8,"Orange", 12f));
            add(new Product(9,"Parsley", 5f));
            add(new Product(10,"Pineapple", 25f));
        }};


        resp.getWriter().println("<H1>Product list</H1>");
        resp.getWriter().println("<table><tr><th>ID</th><th>Title</th><th>Cost</th></tr>");

        for (Product product: prodList) {
            resp.getWriter().println("<tr><td>" + product.getId()+ "</td><td>" + product.getTitle()+ "</td><td>" + product.getCost()+ "</td></tr>");
        }
        resp.getWriter().println("</table>");
    }
}
