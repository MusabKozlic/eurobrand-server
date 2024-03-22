package com.eurobrand.controllers;

import com.eurobrand.dto.NewProductDto;
import com.eurobrand.dto.ProductDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ImagesEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.ProductRepository;
import com.eurobrand.services.ImageService;
import com.eurobrand.services.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@RestController
@RequestMapping("/products")
@CrossOrigin
@RequiredArgsConstructor
public class ProductsController {
    @Autowired
    private ProductService service;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public List<ProductDto> getAllProductsWithImages() {
        List<ProductEntity> allProducts = service.getRepository().findAll();
        List<ProductDto> productDtos = new ArrayList<>();

        for(ProductEntity product : allProducts){
            List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
            ProductDto productDto = new ProductDto(product.getId(), product.getBrand(), product.getModel(), product.getDescription(), product.getStock(), product.getCategory(), product.getProductStatusEntity(),images ,product.getPrice());
            productDtos.add(productDto);
        }

        return  productDtos;
    }

    @PostMapping("/save")
    public void saveProduct(@RequestBody NewProductDto productDto) {
        service.saveProduct(productDto);
    }

    @PostMapping("/products")
    public List<ProductEntity> searchProducts(@RequestBody ProductSearchDto searchDto) {
        return service.searchProducts(searchDto);
    }
}
