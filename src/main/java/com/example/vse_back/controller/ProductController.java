package com.example.vse_back.controller;

import com.example.vse_back.exceptions.ProductIsNotFoundException;
import com.example.vse_back.infrastructure.product.ProductAmountRequest;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.service.ProductService;
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
    @PostMapping("/admin/product")
    public ResponseEntity<?> createProduct(@ModelAttribute @Valid ProductCreationRequest productCreationRequest) {
        productService.create(productCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/admin/product_amount")
    public ResponseEntity<?> changeProductAmount(@RequestBody @Valid ProductAmountRequest productAmountRequest) {
        String productId = productAmountRequest.getProductId();
        ProductEntity product = productService.findByProductId(productId);
        if (product == null) {
            throw new ProductIsNotFoundException(productId);
        }
        productService.changeProductAmount(product, productAmountRequest.getAmount());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        final List<ProductResponse> products = productService.getAllProducts();
        return products != null && !products.isEmpty()
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "productId") UUID productId) {
        final ProductEntity product = productService.getProduct(productId);
        if (product == null) {
            throw new ProductIsNotFoundException(productId.toString());
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productId") UUID productId) {
        final boolean isDeleted = productService.delete(productId);
        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
