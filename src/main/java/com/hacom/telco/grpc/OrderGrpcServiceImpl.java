package com.hacom.telco.grpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.hacom.telco.actor.OrderProcessorActor;
import io.grpc.stub.StreamObserver;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final ActorRef orderProcessorActor;

    public OrderGrpcServiceImpl(ActorSystem actorSystem, ApplicationContext context) {
        this.orderProcessorActor = actorSystem.actorOf(OrderProcessorActor.props(context), "orderProcessorActor");
    }

    @Override
    public void insertOrder(InsertOrderRequest request, StreamObserver<InsertOrderResponse> responseObserver) {
        orderProcessorActor.tell(new OrderProcessorActor.ProcessOrderMsg(request, responseObserver), ActorRef.noSender());
    }
}
