package com.eurobrand.controllers;

import com.eurobrand.dto.BannerDto;
import com.eurobrand.dto.NewProductDto;
import com.eurobrand.dto.ProductDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.ImagesEntity;
import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.services.ImageService;
import com.eurobrand.services.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ProductDto> getAllProductsWithImages(@RequestParam(required = false) String searchParams) {
        List<ProductEntity> allProducts = service.findAllProducts(searchParams);
        List<ProductDto> productDtos = new ArrayList<>();

        for(ProductEntity product : allProducts){
            List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
            ProductDto productDto = new ProductDto(product.getId(), product.getBrand(), product.getModel(), product.getDescription(), product.getDescriptionUrl(), product.getStock(), product.getCategory(), product.getProductStatusEntity(),images ,product.getPrice(), product.getTimestamp());
            productDtos.add(productDto);
        }

        // Sort productDtos by timestamp in descending order
        productDtos.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        return  productDtos;
    }

    @GetMapping("/banner")
    public List<BannerDto> getBannerData() {
        return service.getBannerData();
    }


    @PostMapping("/save")
    public void saveProduct(@RequestBody NewProductDto productDto) {
        service.saveProduct(productDto);
    }

    @PostMapping("/products")
    public List<ProductEntity> searchProducts(@RequestBody ProductSearchDto searchDto) {
        return service.searchProducts(searchDto);
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable String id) {
        return service.getProductById(Integer.valueOf(id));
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable String id) {
         service.deleteProductById(Integer.valueOf(id));
    }

    @GetMapping("/byCategory")
    public List<ProductDto> getProductsByCategory(@RequestParam String category, @RequestParam String search, @RequestParam String status, @RequestParam String sortStatus) {
        return service.getProductsByCategory(category, search, status, sortStatus);
    }

    @GetMapping("/forOrder/{orderId}")
    public List<OrderProductEntity> getProductsForOrder(@PathVariable String orderId) {
        return service.getProductsForOrder(Integer.valueOf(orderId));
    }
}
