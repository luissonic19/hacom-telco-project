package com.hacom.telco.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter processedOrdersCounter(MeterRegistry registry) {
        return Counter.builder("telco_orders_processed_total")
                .description("Total number of processed orders")
                .register(registry);
    }
}
