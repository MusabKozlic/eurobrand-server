package com.eurobrand.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String brand;
    private String model;
    private String description;
    private String descriptionUrl;
    private Integer stock;
    private Integer price;
    @JoinColumn(name = "category_id")
    @ManyToOne
    private CategoryEntity category;

    @JoinColumn(name = "status_id")
    @ManyToOne
    private ProductStatusEntity productStatusEntity;
    private LocalDateTime timestamp;
}
