package com.eurobrand.services;

import com.eurobrand.dto.OrderDetailsDto;
import com.eurobrand.dto.OrderDetailsProductDto;
import com.eurobrand.entities.OrderDetailsEntity;
import com.eurobrand.entities.OrderProductEntity;
import com.eurobrand.entities.ProductEntity;
import com.eurobrand.repositories.OrderDetailsRepository;
import com.eurobrand.repositories.OrderProductRepository;
import com.eurobrand.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class OrderDetailsService {
    @Autowired
    private OrderDetailsRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Transactional
    public OrderDetailsEntity postOrder(OrderDetailsDto orderDetails) {
        OrderDetailsEntity orderDetailsEntity = orderDetails.getOrder();


        OrderDetailsEntity order = repository.save(orderDetailsEntity);

        handleProductSave(orderDetails.getProducts(), order);

        return order;
    }

    private void handleProductSave(List<OrderDetailsProductDto> products, OrderDetailsEntity orderDetailsEntity) {

        for(OrderDetailsProductDto productDto : products){
            OrderProductEntity orderProductEntity = new OrderProductEntity();
            ProductEntity productEntity = productRepository.findById(productDto.getProductId()).orElse(null);


            orderProductEntity.setProduct(productEntity);
            orderProductEntity.setOrderDetails(orderDetailsEntity);
            orderProductEntity.setQuantity(productDto.getQuantity());

            orderProductRepository.save(orderProductEntity);

        }
    }
}
