package com.eurobrand.services;

import com.eurobrand.dto.NewProductDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ImagesEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.entities.ProductStatusEntity;
import com.eurobrand.repositories.CategoryRepository;
import com.eurobrand.repositories.ImageRepository;
import com.eurobrand.repositories.ProductRepository;
import com.eurobrand.repositories.ProductStatusRepository;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
        ProductEntity product = new ProductEntity();

        product.setStock(Integer.valueOf(productDto.getStock()));
        product.setModel(productDto.getModel());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        ProductStatusEntity productStatus = productStatusRepository.findById(productDto.getStatus()).orElse(null);
        CategoryEntity category = categoryRepository.findById(productDto.getCategory()).orElse(null);

        product.setProductStatusEntity(productStatus);
        product.setCategory(category);

        repository.save(product);

        for(String image : productDto.getImages()){
            ImagesEntity imagesEntity = new ImagesEntity();

            imagesEntity.setProduct(product);
            imagesEntity.setImageUrl(image);

            imageRepository.save(imagesEntity);
        }
    }
}
