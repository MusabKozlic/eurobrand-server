package com.eurobrand.repositories;

import com.eurobrand.entities.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Integer> {
}
