package ru.polyan.onlinecart;

public class Product {

    private int id;
    String title;
    float cost;

    public void setTitle(String title) {
        this.title = title;
    }

    public Product() {

    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getCost() {
        return cost;
    }

    public Product(int id, String title, float cost) {
        this.id = id;
        this.title = title;
        this.cost = cost;
    }
}
