package com.eurobrand.dto;

import com.eurobrand.entities.OrderDetailsEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDetailsDto {
    private OrderDetailsEntity order;
    private List<OrderDetailsProductDto> products;
}
