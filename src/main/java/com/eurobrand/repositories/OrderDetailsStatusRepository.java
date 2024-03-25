package com.eurobrand.repositories;

import com.eurobrand.entities.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsStatusRepository extends JpaRepository<OrderStatusEntity, Integer> {
}
