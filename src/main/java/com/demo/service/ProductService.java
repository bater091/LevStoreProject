package com.demo.service;

import com.demo.entity.Product;

import com.demo.repository.ProductRepository;
import com.demo.repository.UserRepository;
import com.demo.validator.ProductValidator;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductValidator productValidator;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    public String uploadPath;
    @Autowired
    public ProductService(ProductRepository productRepository, ProductValidator productValidator, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.productValidator = productValidator;
        this.userRepository = userRepository;
    }

    public MultipartFile savePhoto(MultipartFile file) {
        Path path = Paths.get(uploadPath + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));

        try {

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public Product createProduct(MultipartFile file, Double price, String productName, String username) {

         Path path = Paths.get(uploadPath + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));

        try {

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return productRepository.save(productValidator.validateProduct(new Product
                (
                        productName,
                        path.toString(),
                        file.getOriginalFilename(),
                        price,
                        userRepository.findByUsername(username)

                )
        ));


    }

    public Product getProductByProductName(String productName) {
        return productRepository.findByProductName(productName).orElse(null);
    }

    public ArrayList<Product> getAllProduct(String marker) {
        ArrayList<Product> products = productRepository.findAll();
        if(marker.equals("low")){
        products.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getPrice().intValue() - o2.getPrice().intValue();
            }
        });
        return products;
        }
            if(marker.equals("high")){
                products.sort(new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o2.getPrice().intValue() - o1.getPrice().intValue();
                    }
                });
                return products;
        }
            return products;
        }

        @Transactional
        @SneakyThrows
        public void deleteByName (String product){
            if (Files.exists(Paths.get(getProductByProductName(product).getPath()))) {
                Files.delete(Paths.get(getProductByProductName(product).getPath()));
            }
            productRepository.deleteByProductName(product);


        }
        public ArrayList<Product> findUserProducts(Long id){
        return productRepository.findUserProducts(id);
        }

    }
