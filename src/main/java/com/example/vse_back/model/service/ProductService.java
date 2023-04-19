package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.ProductIsNotFoundException;
import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageService imageService;

    public ProductService(ProductRepository productRepository, ImageService imageService) {
        this.productRepository = productRepository;
        this.imageService = imageService;
    }

    public List<ProductResponse> getAllProducts() {
        final List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(product -> new ProductResponse(
                product.getId().toString(),
                product.getName(),
                product.getPrice(),
                product.getProductImage()
        )).toList();
    }

    public ProductEntity getProductById(UUID id) {
        ProductEntity product = productRepository.findByProductId(id);
        if (product == null) {
            throw new ProductIsNotFoundException(id.toString());
        }
        return product;
    }

    public void createProduct(ProductCreationRequest productCreationRequest) {
        ProductEntity product = new ProductEntity();
        product.setName(productCreationRequest.getName());
        product.setPrice(productCreationRequest.getPrice());
        product.setDescription(productCreationRequest.getDescription());
        product.setAmount(productCreationRequest.getAmount());
        ImageEntity image = imageService.createImage(productCreationRequest.getFile());
        product.setProductImage(image);
        productRepository.save(product);
    }

    public void setProductAmount(ProductEntity product, Integer amount) {
        product.setAmount(amount);
        productRepository.save(product);
    }

    public void setProductAmount(List<OrderCreationDetails> orderCreationDetails) {
        orderCreationDetails.forEach(detail -> {
            ProductEntity product = getProductById(UUID.fromString(detail.getProductId()));
            setProductAmount(product, product.getAmount() - detail.getQuantity());
        });
    }

    public boolean deleteProductById(UUID id) {
        if (productRepository.existsById(id)) {
            imageService.deleteImage(getProductById(id).getProductImage().getId());
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
