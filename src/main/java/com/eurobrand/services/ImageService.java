package com.eurobrand.services;

import com.eurobrand.entities.ImagesEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.ImageRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class ImageService {
    @Autowired private ImageRepository repository;

    public List<ImagesEntity> findImagesForThisProduct(Integer productId) {
        return repository.findAll(buildSpecification(productId));
    }

    private Specification<ImagesEntity> buildSpecification(Integer productId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join the product attribute
            Join<ImagesEntity, ProductEntity> productJoin = root.join("product", JoinType.INNER);

            // Add filters based on search criteria
            predicates.add(criteriaBuilder.equal(productJoin.get("id"), productId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
