package com.eurobrand.services;

import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Getter
@Setter
@Service
@RequiredArgsConstructor
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

}
