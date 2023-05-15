package com.example.vse_back.model.service;

import com.example.vse_back.exceptions.EntityIsNotFoundException;
import com.example.vse_back.exceptions.NotEnoughProductException;
import com.example.vse_back.infrastructure.order_detail.OrderCreationDetails;
import com.example.vse_back.infrastructure.product.ProductChangeRequest;
import com.example.vse_back.infrastructure.product.ProductCreationRequest;
import com.example.vse_back.infrastructure.product.ProductResponse;
import com.example.vse_back.model.entity.ImageEntity;
import com.example.vse_back.model.entity.ProductEntity;
import com.example.vse_back.model.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImage()
        )).toList();
    }

    public ProductEntity getProductById(UUID id) {
        ProductEntity product = productRepository.findByProductId(id);
        if (product == null) {
            throw new EntityIsNotFoundException("product", id);
        }
        return product;
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

    public void changeProduct(ProductChangeRequest productChangeRequest) {
        ProductEntity product = getProductById(productChangeRequest.getProductId());
        product.setName(productChangeRequest.getName());
        product.setPrice(productChangeRequest.getPrice());
        product.setDescription(productChangeRequest.getDescription());
        product.setAmount(productChangeRequest.getAmount());
        product.setImage(setupImage(product, productChangeRequest.getFile()));
        productRepository.save(product);
    }

    private ImageEntity setupImage(ProductEntity product, MultipartFile file) {
        if (product.getImage() != null) {
            imageService.deleteImage(product.getImage().getId());
        }
        return imageService.createAndGetImage(file);
    }

    public void setupProductAmount(ProductEntity product, Integer amount) {
        product.setAmount(amount);
        productRepository.save(product);
    }

    public void setupProductAmount(List<OrderCreationDetails> orderCreationDetails) {
        for (OrderCreationDetails detail : orderCreationDetails) {
            ProductEntity product = getProductById(detail.getProductId());
            Integer wantedAmount = detail.getQuantity();
            Integer realAmount = product.getAmount();
            if (realAmount < wantedAmount) {
                throw new NotEnoughProductException(product.getId(), wantedAmount, realAmount);
            }
            setupProductAmount(product, product.getAmount() - detail.getQuantity());
        }
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
