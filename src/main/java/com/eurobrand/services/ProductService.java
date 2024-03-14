package com.eurobrand.services;

import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.ProductRepository;
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
}
