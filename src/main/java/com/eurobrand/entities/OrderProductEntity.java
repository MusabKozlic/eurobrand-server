package com.eurobrand.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "order_products")
@Getter
@Setter
public class OrderProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "product_id")
    @ManyToOne
    private ProductEntity product;

    @JoinColumn(name = "order_id")
    @ManyToOne
    private OrderDetailsEntity orderDetails;

    private Integer quantity;
}
