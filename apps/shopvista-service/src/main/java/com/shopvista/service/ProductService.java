package com.shopvista.service;

import com.shopvista.dto.ProductRequest;
import com.shopvista.dto.ProductResponse;
import com.shopvista.entity.Product;
import com.shopvista.repository.ProductRepository;
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

    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());
        
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching all active products");
        return productRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public ProductResponse getProductBySku(String sku) {
        log.info("Fetching product with SKU: {}", sku);
        return productRepository.findBySku(sku)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.searchByName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : product.getIsActive());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    public void deactivateProduct(Long id) {
        log.info("Deactivating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product deactivated successfully with ID: {}", id);
    }

    private ProductResponse mapToResponse(Product product) {
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
