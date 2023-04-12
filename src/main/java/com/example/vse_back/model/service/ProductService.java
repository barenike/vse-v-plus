package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.ProductIsNotFoundException;
import com.example.vse_back.infrastructure.order.OrderCreationDetails;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.repository.ProductRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final DropboxService dropboxService;

    public ProductService(ProductRepository productRepository, DropboxService dropboxService) {
        this.productRepository = productRepository;
        this.dropboxService = dropboxService;
    }

    public List<ProductResponse> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(product -> new ProductResponse(
                product.getId().toString(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        )).collect(Collectors.toList());
    }

    public ProductEntity getProduct(UUID id) {
        return productRepository.findByProductId(id);
    }

    public void create(ProductCreationRequest productCreationRequest) {
        ProductEntity product = new ProductEntity();
        product.setName(productCreationRequest.getName());
        product.setPrice(productCreationRequest.getPrice());
        product.setDescription(productCreationRequest.getDescription());
        product.setAmount(productCreationRequest.getAmount());
        String extension = FilenameUtils.getExtension(productCreationRequest.getFile().getOriginalFilename());
        String imagePath = String.format("/%s.%s", product.getName(), extension);
        product.setImagePath(imagePath);
        String imageUrl;
        try {
            imageUrl = dropboxService.upload(imagePath, productCreationRequest.getFile().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }

    // Maybe, I will refactor this later
    public void changeProductAmount(String productId, Integer amount) {
        ProductEntity product = findByProductId(productId);
        if (product == null) {
            throw new ProductIsNotFoundException(productId);
        }
        product.setAmount(amount);
        productRepository.save(product);
    }

    public void changeProductAmount(ProductEntity product, Integer amount) {
        product.setAmount(amount);
        productRepository.save(product);
    }

    public void changeProductAmount(List<OrderCreationDetails> orderCreationDetails) {
        orderCreationDetails.forEach(details -> changeProductAmount(details.getProductId(),
                findByProductId(details.getProductId()).getAmount() - details.getQuantity()));
    }

    public ProductEntity findByProductId(String productId) {
        return productRepository.findByProductId(UUID.fromString(productId));
    }

    public boolean delete(UUID id) {
        if (productRepository.existsById(id)) {
            dropboxService.delete(getProduct(id).getImagePath());
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
