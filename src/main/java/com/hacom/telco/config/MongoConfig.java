package com.hacom.telco.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.hacom.telco.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${mongodbDatabase}")
    private String databaseName;

    @Value("${mongodbUri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(mongoUri);
    }
}
