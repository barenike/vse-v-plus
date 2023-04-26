package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.ProductIsNotFoundException;
import com.example.vse_back.infrastructure.order_detail.OrderCreationDetails;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
                product.getImage()
        )).toList();
    }

    public ProductEntity getProductById(UUID id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ProductIsNotFoundException(id.toString());
        }
        return product.get();
    }

    public void createProduct(ProductCreationRequest productCreationRequest) {
        ProductEntity product = new ProductEntity();
        product.setName(productCreationRequest.getName());
        product.setPrice(productCreationRequest.getPrice());
        product.setDescription(productCreationRequest.getDescription());
        product.setAmount(productCreationRequest.getAmount());
        ImageEntity image = imageService.createAndGetImage(productCreationRequest.getFile());
        product.setImage(image);
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

    // I think that I shouldn't delete the product in the case that it has at least one order
    // Instead, shall I add some flag to DB table?
    public boolean deleteProductById(UUID id) {
        if (productRepository.existsById(id)) {
            ImageEntity image = getProductById(id).getImage();
            productRepository.deleteById(id);
            imageService.deleteImage(image.getId());
            return true;
        }
        return false;
    }
}
