package com.eurobrand.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewProductDto {
    private String brand;
    private String description;
    private String model;
    private String stock;
    private Integer category;
    private Integer status;
    private Integer price;
    private List<String> images;
}
