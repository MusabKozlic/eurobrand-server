package com.eurobrand.repositories;

import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ImagesEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImagesEntity, Integer> {
    List<ImagesEntity> findAll(Specification<ImagesEntity> imagesEntitySpecification);
}
