package com.example.vse_back.controller;

import com.example.vse_back.infrastructure.product.ProductAmountRequest;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Failure tests is possibly needed.
    @Operation(summary = "Create the product")
    @PostMapping("/admin/product")
    public ResponseEntity<?> createProduct(@ModelAttribute @Valid ProductCreationRequest productCreationRequest) {
        productService.createProduct(productCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Change amount of the product")
    @PostMapping("/admin/product_amount")
    public ResponseEntity<?> changeProductAmount(@RequestBody @Valid ProductAmountRequest productAmountRequest) {
        String productId = productAmountRequest.getProductId();
        ProductEntity product = productService.getProductById(UUID.fromString(productId));
        productService.changeProductAmount(product, productAmountRequest.getAmount());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get all products")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        final List<ProductResponse> products = productService.getAllProducts();
        return products != null && !products.isEmpty()
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get the product")
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "productId") UUID productId) {
        final ProductEntity product = productService.getProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Delete the product")
    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productId") UUID productId) {
        final boolean isDeleted = productService.deleteProductById(productId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
