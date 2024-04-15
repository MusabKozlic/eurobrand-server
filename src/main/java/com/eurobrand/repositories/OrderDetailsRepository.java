package com.eurobrand.repositories;

import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Integer> {
    List<OrderDetailsEntity> findAll(Specification<OrderDetailsEntity> productEntitySpecification);
}
