package com.eurobrand.services;

import com.eurobrand.dto.BannerDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

            // Add order by clause
            query.orderBy(criteriaBuilder.desc(root.get("timestamp")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public void saveProduct(NewProductDto productDto) {
        ProductEntity product = productDto.getId() != null ? repository.findById(productDto.getId()).orElse(null) : new ProductEntity();
    if(product != null){
        product.setStock(productDto.getStock());
        product.setModel(productDto.getModel());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setDescriptionUrl(productDto.getDescriptionUrl());
        product.setTimestamp(LocalDateTime.now());
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

    public List<ProductDto> getProductsByCategory(String category, String search, String status, String sortStatus) {
        List<ProductEntity> allProducts = repository.findAll(buildSpecificationByCategory(category, search, status, sortStatus));
        List<ProductDto> productDtos = new ArrayList<>();

        for(ProductEntity product : allProducts){
            List<ImagesEntity> images = imageService.findImagesForThisProduct(product.getId());
            ProductDto productDto = new ProductDto(product.getId(), product.getBrand(), product.getModel(), product.getDescription(), product.getDescriptionUrl(), product.getStock(), product.getCategory(), product.getProductStatusEntity(),images ,product.getPrice(), product.getTimestamp());
            productDtos.add(productDto);
        }

        // Sort productDtos by timestamp in descending order
        if(sortStatus == null || sortStatus.isEmpty()){
            productDtos.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        }

        return productDtos;
    }

    private Specification<ProductEntity> buildSpecificationByCategory(String category, String search, String status, String sortStatus) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isEmpty()) {
                Join<ProductEntity, CategoryEntity> categoryJoin = root.join("category");

                if ("računarska oprema".equals(category)) {
                    List<String> racunarskaOpremaSlugs = Arrays.asList(
                            "adapteri", "misevi", "projektori", "diskovi", "tastature", "graficke kartice", "računarska oprema"
                    );
                    predicates.add(categoryJoin.get("slug").in(racunarskaOpremaSlugs));
                } else if ("laptopi".equals(category)) {
                    List<String> laptopiSlugs = Arrays.asList(
                            "laptopi novi", "brandname laptopi", "surface", "tableti", "laptopi"
                    );
                    predicates.add(categoryJoin.get("slug").in(laptopiSlugs));
                } else if ("racunari".equals(category)) {
                    List<String> racunariSlugs = Arrays.asList(
                            "racunari novi", "gaming racunari", "brandname racunari", "workstation", "all-in-one", "racunari"
                    );
                    predicates.add(categoryJoin.get("slug").in(racunariSlugs));
                } else {
                    predicates.add(criteriaBuilder.equal(categoryJoin.get("slug"), category));
                }
            }

            if (search != null && !search.isEmpty()) {
                String[] searchTerms = search.toLowerCase().split("\\s+"); // Split search term by spaces

                List<Predicate> searchPredicates = new ArrayList<>();
                for (String term : searchTerms) {
                    List<Predicate> termPredicates = new ArrayList<>();
                    termPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + term + "%"));
                    termPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + term + "%"));

                    // Combine all predicates for this term using OR
                    searchPredicates.add(criteriaBuilder.or(termPredicates.toArray(new Predicate[termPredicates.size()])));
                }

                // Combine all term predicates using AND
                predicates.add(criteriaBuilder.and(searchPredicates.toArray(new Predicate[searchPredicates.size()])));
            }

            if (status != null && !status.isEmpty()) {
                Join<ProductEntity, ProductStatusEntity> statusJoin = root.join("productStatusEntity");
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(statusJoin.get("status")), status.toLowerCase()));
            }

            // Order by sortStatus
            if ("asc".equalsIgnoreCase(sortStatus)) {
                query.orderBy(criteriaBuilder.asc(root.get("price")));
            } else if ("desc".equalsIgnoreCase(sortStatus)) {
                query.orderBy(criteriaBuilder.desc(root.get("price")));
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

    public List<BannerDto> getBannerData() {
        List<ImagesEntity> images = imageRepository.findAll();

        // Group images by product ID and collect the first image URL for each product
        Map<Integer, String> productImages = images.stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ImagesEntity::getImageUrl,
                        (url1, url2) -> url1 // Merge function to keep the first URL
                ));

        // Create BannerDto objects from the grouped data
        List<BannerDto> bannerDtos = new ArrayList<>();
        productImages.forEach((productId, imageUrl) -> {
            BannerDto bannerDto = new BannerDto();
            bannerDto.setProductId(productId);
            bannerDto.setBannerImageUrl(imageUrl);
            bannerDtos.add(bannerDto);
        });

        return bannerDtos;
    }
}
