package com.example.vse_back.infrastructure.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private Integer price;
    private String imageUrl;
}
