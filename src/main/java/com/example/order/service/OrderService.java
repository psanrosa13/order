package com.example.order.service;

import com.example.order.domain.NewOrder;
import com.example.order.domain.Order;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    public Order save(NewOrder newOrder) {
        return new Order(UUID.randomUUID(),true, "IN_PROCESS");
    }
}
