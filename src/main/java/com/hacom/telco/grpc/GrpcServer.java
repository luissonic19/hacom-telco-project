package com.hacom.telco.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GrpcServer {
    private static final Logger logger = LogManager.getLogger(GrpcServer.class);
    private Server server;
    private final OrderGrpcServiceImpl orderGrpcService;

    public GrpcServer(OrderGrpcServiceImpl orderGrpcService) {
        this.orderGrpcService = orderGrpcService;
    }

    @PostConstruct
    public void start() throws IOException {
        int port = 9090;
        server = ServerBuilder.forPort(port)
                .addService(orderGrpcService)
                .build()
                .start();
        logger.info("gRPC server started, listening on {}", port);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            logger.info("Shutting down gRPC server");
            server.shutdown();
        }
    }
}
