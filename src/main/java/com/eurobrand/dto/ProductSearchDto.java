package com.eurobrand.dto;

import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ProductStatusEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductSearchDto {
    private Integer id;
    private String brand;
    private String model;
    private String description;
    private Integer stock;
    private String category_id;
    private String category;
    private String productStatus_id;
}
