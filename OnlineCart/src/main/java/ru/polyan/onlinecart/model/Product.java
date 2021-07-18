package ru.polyan.onlinecart.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="products")
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="title")
    private String title;
    @Column(name="price")
    private double price;

    public Product(Long id, String title, double price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }
}
