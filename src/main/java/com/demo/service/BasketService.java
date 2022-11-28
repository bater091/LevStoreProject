package com.demo.service;

import com.demo.entity.Basket;
import com.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BasketService {
    private final Basket basket;
    @Autowired
    public BasketService(Basket basket) {
        this.basket = basket;
    }
    public void addProduct(Product product, String amount){
         basket.addProduct(product,amount);

    }
    public Map<Product,String> getUserBasket(){
        return basket.getUserBasket();
    }
    public void clearBasket(){basket.clearBasket();}

}
