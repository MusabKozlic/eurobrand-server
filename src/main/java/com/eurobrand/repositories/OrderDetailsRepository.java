package com.eurobrand.repositories;

import com.eurobrand.entities.OrderDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Integer> {
}
