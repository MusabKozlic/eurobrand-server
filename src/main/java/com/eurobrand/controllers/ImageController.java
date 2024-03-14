package com.eurobrand.controllers;

import com.eurobrand.services.ImageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Getter
@RestController
@RequestMapping("/images")
@CrossOrigin
@RequiredArgsConstructor
public class ImageController {
    @Autowired
    private ImageService service;
}
