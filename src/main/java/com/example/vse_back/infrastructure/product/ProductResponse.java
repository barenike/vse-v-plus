package com.example.vse_back.infrastructure.product;

import com.example.vse_back.model.entity.ImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductResponse {
    private UUID id;

    private String name;

    private Integer price;

    private ImageEntity image;
}
