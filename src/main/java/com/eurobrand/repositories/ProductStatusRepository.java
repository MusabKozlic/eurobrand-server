package com.eurobrand.repositories;

import com.eurobrand.entities.ProductStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusRepository extends JpaRepository<ProductStatusEntity, Integer> {
}
