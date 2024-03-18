package com.eurobrand.controllers;
import com.eurobrand.dto.OrderDetailsDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.services.OrderDetailsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@RestController
@RequestMapping("/orders")
@CrossOrigin
@RequiredArgsConstructor
public class OrderDetailsController {
    @Autowired
    private OrderDetailsService service;

    @PostMapping
    public OrderDetailsEntity postOrder(@RequestBody OrderDetailsDto order) {
        return service.postOrder(order);
    }
}
