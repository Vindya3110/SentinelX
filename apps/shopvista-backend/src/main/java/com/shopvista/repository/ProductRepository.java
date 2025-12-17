package com.shopvista.repository;

import com.shopvista.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findAllActive();
    
    @Query("SELECT p FROM Product p WHERE p.category = ?1 AND p.isActive = true")
    List<Product> findByCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% AND p.isActive = true")
    List<Product> searchByName(String name);
}
