package com.demo.repository;

import com.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;

import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductName(String productName);
    ArrayList<Product> findAll();
    @Modifying
    @Query("delete Product n where n.productName = ?1")
    void deleteByProductName(String productName);

    @Query("select n from Product n  where user_id = ?1")
    ArrayList<Product> findUserProducts(Long userId);
}
