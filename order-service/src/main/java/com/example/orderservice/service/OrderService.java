package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.Order;

public interface OrderService {

    void createOrder(OrderDto orderDto);

    OrderDto getOrderByOrderId(String orderId);

    Iterable<Order> getOrdersByUserId(String userId);
}
