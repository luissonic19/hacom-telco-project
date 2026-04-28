package com.hacom.telco.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hacom.telco.grpc.InsertOrderRequest;
import com.hacom.telco.grpc.InsertOrderResponse;
import com.hacom.telco.model.Order;
import com.hacom.telco.repository.OrderRepository;
import com.hacom.telco.smpp.SmppClientService;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class OrderProcessorActor extends AbstractActor {
    private static final Logger logger = LogManager.getLogger(OrderProcessorActor.class);

    private final OrderRepository orderRepository;
    private final SmppClientService smppClientService;
    private final Counter processedOrdersCounter;

    public OrderProcessorActor(ApplicationContext context) {
        this.orderRepository = context.getBean(OrderRepository.class);
        this.smppClientService = context.getBean(SmppClientService.class);
        this.processedOrdersCounter = context.getBean("processedOrdersCounter", Counter.class);
    }

    public static Props props(ApplicationContext context) {
        return Props.create(OrderProcessorActor.class, () -> new OrderProcessorActor(context));
    }

    public static class ProcessOrderMsg {
        public final InsertOrderRequest request;
        public final StreamObserver<InsertOrderResponse> responseObserver;

        public ProcessOrderMsg(InsertOrderRequest request, StreamObserver<InsertOrderResponse> responseObserver) {
            this.request = request;
            this.responseObserver = responseObserver;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessOrderMsg.class, this::processOrder)
                .build();
    }

    private void processOrder(ProcessOrderMsg msg) {
        InsertOrderRequest req = msg.request;
        logger.info("Processing order: {}", req.getOrderId());

        Order order = new Order(
                req.getOrderId(),
                req.getCustomerId(),
                req.getCustomerPhoneNumber(),
                "PROCESSED",
                new ArrayList<>(req.getItemsList()),
                OffsetDateTime.now()
        );

        orderRepository.save(order).subscribe(savedOrder -> {
            logger.info("Order saved to MongoDB: {}", savedOrder.getOrderId());
            processedOrdersCounter.increment();
            
            // Send SMS
            String smsText = "Your order " + savedOrder.getOrderId() + " has been processed";
            smppClientService.sendSms(savedOrder.getCustomerPhoneNumber(), smsText);
            
            // Reply via gRPC
            InsertOrderResponse response = InsertOrderResponse.newBuilder()
                    .setOrderId(savedOrder.getOrderId())
                    .setStatus(savedOrder.getStatus())
                    .build();
            msg.responseObserver.onNext(response);
            msg.responseObserver.onCompleted();
        }, error -> {
            logger.error("Error processing order", error);
            msg.responseObserver.onError(error);
        });
    }
}
