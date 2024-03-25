package com.eurobrand.dto;

import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ImagesEntity;
import com.eurobrand.entities.ProductStatusEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto {
    private Integer id;
    private String brand;
    private String model;
    private String description;
    private Integer stock;
    private CategoryEntity category;
    private ProductStatusEntity productStatus;
    private List<ImagesEntity> images;
    private Integer price;

    public ProductDto(Integer id, String brand, String model, String description, Integer stock, CategoryEntity category, ProductStatusEntity productStatus, List<ImagesEntity> images, Integer price) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.stock = stock;
        this.category = category;
        this.productStatus = productStatus;
        this.images = images;
        this.price = price;
    }

    public ProductDto() {}
}
