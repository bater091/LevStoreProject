package com.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor

public class Product {
    @Id
    @Column
            (
            name = "productName",
            unique = true,
            nullable = false
    )
    private String productName;
    @Column
    private String path;
    @Column(
            unique = true
    )
    private String fileName;
    @Column
    private Double price;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Product(String productName, String path, String fileName, Double price, User user) {
        this.productName = productName;
        this.path = path;
        this.fileName = fileName;
        this.price = price;
        this.user = user;
    }
}
