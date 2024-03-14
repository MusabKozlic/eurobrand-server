package com.eurobrand.controllers;

import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.ProductRepository;
import com.eurobrand.services.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@RestController
@RequestMapping("/products")
@CrossOrigin
@RequiredArgsConstructor
public class ProductsController {
    @Autowired
    private ProductService service;

    @GetMapping
    public List<ProductEntity> getAllCategories() {
        return service.getRepository().findAll();
    }

    @PostMapping("/products")
    public List<ProductEntity> searchProducts(@RequestBody ProductSearchDto searchDto) {
        return service.searchProducts(searchDto);
    }
}
