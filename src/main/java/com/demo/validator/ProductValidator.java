package com.demo.validator;

import com.demo.entity.Product;
import com.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {
    private final ProductRepository productRepository;
    @Autowired
    public ProductValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Product validateProduct(Product product){
        if(productRepository.findByProductName(product.getProductName()).isEmpty()){
            return product;
        }else {
            throw new RuntimeException("product is exist");
        }
    }
}
