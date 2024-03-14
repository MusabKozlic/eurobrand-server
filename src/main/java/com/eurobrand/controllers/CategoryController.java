package com.eurobrand.controllers;

import com.eurobrand.entities.CategoryEntity;
import com.eurobrand.services.CategoryService;
import jakarta.persistence.Access;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Getter
@RestController
@RequestMapping("/category")
@CrossOrigin
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping
    public List<CategoryEntity> getAllCategories() {
        return service.getRepository().findAll();
    }

}
