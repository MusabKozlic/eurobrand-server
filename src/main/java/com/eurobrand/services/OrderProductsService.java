package com.eurobrand.services;

import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.OrderProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Service
public class OrderProductsService {
    @Autowired
    private OrderProductRepository repository;

    public void deleteByProductId(Integer productId) {
        List<OrderProductEntity> productEntityList = repository.findAll(buildSpecification(productId));
        repository.deleteAll(productEntityList);
    }

    private Specification<OrderProductEntity> buildSpecification(Integer productId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(productId != null) {
                Join<OrderProductEntity, ProductEntity> productJoin = root.join("product");
                predicates.add(criteriaBuilder.equal(productJoin.get("id"), productId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
