package com.sentries.SentinelX.app_server.repository;

import com.sentries.SentinelX.app_server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByIsActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String name);
}
