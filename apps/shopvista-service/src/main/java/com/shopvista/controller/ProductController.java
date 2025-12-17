package com.shopvista.controller;

import com.shopvista.dto.ProductRequest;
import com.shopvista.dto.ProductResponse;
import com.shopvista.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private final ProductService productService;

    /**
     * POST - Create a new product
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        log.info("POST request to create new product");
        ProductResponse product = productService.createProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    /**
     * GET - Retrieve all products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("GET request to retrieve all products");
        List<ProductResponse> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET - Retrieve all active products
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        log.info("GET request to retrieve active products");
        List<ProductResponse> products = productService.getActiveProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET - Retrieve product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("GET request to retrieve product with id: {}", id);
        try {
            ProductResponse product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET - Retrieve product by SKU
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        log.info("GET request to retrieve product with sku: {}", sku);
        try {
            ProductResponse product = productService.getProductBySku(sku);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with sku: {}", sku);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET - Retrieve products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.info("GET request to retrieve products by category: {}", category);
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET - Search products by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        log.info("GET request to search products by name: {}", name);
        List<ProductResponse> products = productService.searchProducts(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * PUT - Update a product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        log.info("PUT request to update product with id: {}", id);
        try {
            ProductResponse product = productService.updateProduct(id, request);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE - Delete a product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE request to delete product with id: {}", id);
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PATCH - Deactivate a product
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        log.info("PATCH request to deactivate product with id: {}", id);
        try {
            productService.deactivateProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Product not found with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
