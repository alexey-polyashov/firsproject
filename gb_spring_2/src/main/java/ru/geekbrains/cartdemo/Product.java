package ru.geekbrains.cartdemo;

public class Product {
    private int id;
    private String title;
    private Float cost;

    public Product(int id, String title, Float cost) {
        this.id = id;
        this.title = title;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getCost() {
        return cost;
    }
}
