package com.demo.entity;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component

public class Basket {
    private final HashMap<Product, String> basket = new HashMap<>();


    public void addProduct(Product product, String amount){
        basket.put(product, amount);

    }
    public Map<Product , String > getUserBasket(){

        return basket;
    }
    public void clearBasket(){
        basket.clear();
    }

}
