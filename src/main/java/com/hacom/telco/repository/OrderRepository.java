package com.hacom.telco.repository;

import com.hacom.telco.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.time.OffsetDateTime;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
    Mono<Order> findByOrderId(String orderId);
    Flux<Order> findByTsBetween(OffsetDateTime start, OffsetDateTime end);
}
