package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.product.ProductChangeRequest;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Failure tests are possibly needed
    @Operation(summary = "Create the product")
    @PostMapping("/admin/product")
    public ResponseEntity<Object> createProduct(@ModelAttribute @Valid ProductCreationRequest productCreationRequest) {
        productService.createProduct(productCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Failure tests are possibly needed
    @Operation(summary = "Change the product")
    @PostMapping("/admin/product/change")
    public ResponseEntity<Object> changeProduct(@ModelAttribute @Valid ProductChangeRequest productChangeRequest) {
        productService.changeProduct(productChangeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all products")
    @GetMapping("/common/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        final List<ProductResponse> products = productService.getAllProducts();
        return products != null && !products.isEmpty()
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get the product")
    @GetMapping("/common/products/{productId}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable(name = "productId") UUID productId) {
        final ProductEntity product = productService.getProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Delete the product")
    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(name = "productId") UUID productId) {
        final boolean isDeleted = productService.deleteProductById(productId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
