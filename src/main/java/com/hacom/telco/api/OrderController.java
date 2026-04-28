package com.hacom.telco.api;

import com.hacom.telco.model.Order;
import com.hacom.telco.repository.OrderRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{orderId}/status")
    public Mono<String> getOrderStatus(@PathVariable String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(Order::getStatus)
                .defaultIfEmpty("NOT_FOUND");
    }

    @GetMapping("/count")
    public Mono<Long> getOrderCountByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {
        return orderRepository.findByTsBetween(start, end).count();
    }
}
