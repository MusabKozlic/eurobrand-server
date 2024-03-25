package com.eurobrand.controllers;
import com.eurobrand.dto.OrderDetailsDto;
import com.eurobrand.dto.ProductSearchDto;
import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.services.OrderDetailsService;
import jakarta.mail.MessagingException;
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
    public OrderDetailsEntity postOrder(@RequestBody OrderDetailsDto order) throws MessagingException {
        return service.postOrder(order);
    }

    @GetMapping
    public List<OrderDetailsEntity> getOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDetailsEntity getOrderById(@PathVariable String id) {
        return service.getOrderById(Integer.valueOf(id));
    }

    @PostMapping("/{id}/delivery")
    public OrderDetailsEntity handleDelivery(@PathVariable String id) {
        return service.checkDeliveredOrder(Integer.valueOf(id));
    }
}
