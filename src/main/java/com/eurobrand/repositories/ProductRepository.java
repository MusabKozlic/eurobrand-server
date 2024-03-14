package com.eurobrand.repositories;

import com.eurobrand.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findAll(Specification<ProductEntity> productEntitySpecification);
}
