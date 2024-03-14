package com.eurobrand.repositories;

import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
