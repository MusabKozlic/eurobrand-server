package com.eurobrand.services;

import com.eurobrand.dto.NewProductDto;
import com.eurobrand.dto.ProductDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.*;
import com.eurobrand.repositories.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductStatusRepository productStatusRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderProductsService orderProductsService;

    @Transactional
    public List<ProductEntity> searchProducts(ProductSearchDto searchDto) {
        return repository.findAll(buildSpecification(searchDto));
    }

    private Specification<ProductEntity> buildSpecification(ProductSearchDto searchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add filters based on search criteria
            if (searchDto.getBrand() != null) {
                Path<String> brandPath = root.get("brand"); // Cast to Path<String>
                predicates.add(criteriaBuilder.equal(brandPath, searchDto.getBrand()));
            }
            if (searchDto.getModel() != null) {
                Path<String> modelPath = root.get("model"); // Cast to Path<String>
                predicates.add(criteriaBuilder.equal(modelPath, searchDto.getModel()));
            }
            if (searchDto.getDescription() != null) {
                Path<String> descriptionPath = root.get("description"); // Cast to Path<String>
                predicates.add(criteriaBuilder.equal(descriptionPath, searchDto.getDescription()));
            }
            if(searchDto.getId() != null) {
                Path<String> idPath = root.get("id");
                predicates.add(criteriaBuilder.equal(idPath, searchDto.getId()));
            }
            if(searchDto.getProductStatus_id() != null){
                Path<String> productStatusPath = root.get("status_id");
                predicates.add(criteriaBuilder.equal(productStatusPath, searchDto.getProductStatus_id()));
            }
            if(searchDto.getCategory_id() != null){
                Path<String> productStatusPath = root.get("category_id");
                predicates.add(criteriaBuilder.equal(productStatusPath, searchDto.getCategory_id()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public void saveProduct(NewProductDto productDto) {
        ProductEntity product = productDto.getId() != null ? repository.findById(productDto.getId()).orElse(null) : new ProductEntity();
    if(product != null){
        product.setStock(Integer.valueOf(productDto.getStock()));
        product.setModel(productDto.getModel());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        ProductStatusEntity productStatus = productStatusRepository.findById(productDto.getStatus()).orElse(null);
        CategoryEntity category = categoryRepository.findById(productDto.getCategory()).orElse(null);

        product.setProductStatusEntity(productStatus);
        product.setCategory(category);

        repository.saveAndFlush(product);
    }

        assert product != null;
        List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
        imageService.deleteImages(images);

        for(String image : productDto.getImages()){
            ImagesEntity imagesEntity = new ImagesEntity();

            imagesEntity.setProduct(product);
            imagesEntity.setImageUrl(image);

            imageService.saveImage(imagesEntity);
        }
    }

    public List<ProductDto> getProductsByCategory(String category, String search, String status) {
        List<ProductEntity> allProducts = repository.findAll(buildSpecificationByCategory(category, search, status));
        List<ProductDto> productDtos = new ArrayList<>();

        for(ProductEntity product : allProducts){
            List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
            ProductDto productDto = new ProductDto(product.getId(), product.getBrand(), product.getModel(), product.getDescription(), product.getStock(), product.getCategory(), product.getProductStatusEntity(),images ,product.getPrice());
            productDtos.add(productDto);
        }

        return productDtos;
    }

    private Specification<ProductEntity> buildSpecificationByCategory(String category, String search, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(category != null && !category.isEmpty()) {
                Join<ProductEntity, CategoryEntity> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("slug"), category));
            }
            if (search != null && !search.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + search.toLowerCase() + "%"));
            }
            if (status != null && !status.isEmpty()) {
                Join<ProductEntity, ProductStatusEntity> statusJoin = root.join("productStatusEntity");
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(statusJoin.get("status")), status.toLowerCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public ProductDto getProductById(Integer id) {
        ProductEntity product = repository.findById(id).orElse(null);
            ProductDto productDto = new ProductDto();
        if(product != null){
            productDto.setId(product.getId());
            productDto.setProductStatus(product.getProductStatusEntity());
            productDto.setModel(product.getModel());
            productDto.setDescription(product.getDescription());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setStock(product.getStock());
            List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
            productDto.setImages(images);
        }
        return productDto;
    }

    public void deleteProductById(Integer productId) {
        List<ImagesEntity> images = imageService.findImagesForThisProduct(productId);

        imageRepository.deleteAll(images);
        orderProductsService.deleteByProductId(productId);
        repository.deleteById(productId);
    }

    public List<OrderProductEntity> getProductsForOrder(Integer orderId) {
        return orderProductRepository.findAll(buildSpecificationForOrderProducts(orderId));
    }

    private Specification<OrderProductEntity> buildSpecificationForOrderProducts(Integer orderId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(orderId != null) {
                Join<OrderProductEntity, OrderDetailsEntity> orderJoin = root.join("orderDetails");
                predicates.add(criteriaBuilder.equal(orderJoin.get("id"), orderId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
