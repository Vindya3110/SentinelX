package com.sentries.SentinelX.app_server.service;

import com.sentries.SentinelX.app_server.dto.ProductRequest;
import com.sentries.SentinelX.app_server.dto.ProductResponse;
import com.sentries.SentinelX.app_server.entity.Product;
import com.sentries.SentinelX.app_server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get all products
     */
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active products
     */
    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching active products");
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /**
     * Get product by SKU
     */
    public ProductResponse getProductBySku(String sku) {
        log.info("Fetching product with sku: {}", sku);
        return productRepository.findBySku(sku)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found with sku: " + sku));
    }

    /**
     * Get products by category
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products for category: {}", category);
        return productRepository.findByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search products by name
     */
    public List<ProductResponse> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new product
     */
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating new product with sku: {}", productRequest.getSku());

        if (productRepository.findBySku(productRequest.getSku()).isPresent()) {
            throw new RuntimeException("Product with sku already exists: " + productRequest.getSku());
        }

        Product product = new Product();
        product.setSku(productRequest.getSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setIsActive(productRequest.getIsActive() != null ? productRequest.getIsActive() : true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    /**
     * Update an existing product
     */
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!product.getSku().equals(productRequest.getSku())
                && productRepository.findBySku(productRequest.getSku()).isPresent()) {
            throw new RuntimeException("Product with sku already exists: " + productRequest.getSku());
        }

        product.setSku(productRequest.getSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setIsActive(productRequest.getIsActive() != null ? productRequest.getIsActive() : true);

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());

        return convertToResponse(updatedProduct);
    }

    /**
     * Delete a product
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImageUrl(),
                product.getIsActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
