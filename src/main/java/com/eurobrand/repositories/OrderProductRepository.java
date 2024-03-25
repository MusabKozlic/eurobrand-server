package com.eurobrand.repositories;

import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Integer> {
    List<OrderProductEntity> findAll(Specification<OrderProductEntity> productEntitySpecification);
}
