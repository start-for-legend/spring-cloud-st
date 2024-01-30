package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.dto.RequestOrder;
import com.example.orderservice.dto.ResponseOrder;
import com.example.orderservice.entity.Order;
import com.example.orderservice.messageQueue.KafkaProducer;
import com.example.orderservice.messageQueue.OrderProducer;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<HttpStatus> createOrder(@PathVariable String userId, @RequestBody RequestOrder orderRequest) {

        log.info("Before add orders data");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /* jpa */
        OrderDto orderDto = modelMapper.map(orderRequest, OrderDto.class);
        orderDto.setUserId(userId);
        orderService.createOrder(orderDto);

/*        *//* kafka *//*
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        *//* send this order to the kafka *//*
        kafkaProducer.send("example-catalog-topic", orderDto);
        orderProducer.send("orders", orderDto);*/

        log.info("After added orders data");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable String userId) {
        log.info("Before retrieve orders data");

        Iterable<Order> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach(v -> result.add(new ModelMapper().map(v, ResponseOrder.class)));

        log.info("After retrieved orders data");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
