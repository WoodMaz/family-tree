package com.example.config.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@PropertySource("classpath:application.properties")
@Slf4j
public class MongoDBConfig extends AbstractMongoClientConfiguration {
    @Autowired
    private Environment env;
    
    @Override
    protected String getDatabaseName() {
        return env.getProperty("spring.data.mongodb.database");
    }

    private String getHost() {
        return env.getProperty("spring.data.mongodb.host");
    }

    private String getPort() {
        return env.getProperty("spring.data.mongodb.port");
    }

    private String getUsername() {
        return env.getProperty("spring.data.mongodb.username");
    }

    private String getPassword() {
        return env.getProperty("spring.data.mongodb.password");
    }

    @Override
    public MongoClient mongoClient() {
        StringBuilder mongoUrl = new StringBuilder();
        mongoUrl.append("mongodb://");
        mongoUrl.append(getUsername() + ":");
        mongoUrl.append(getPassword() + "@");
        mongoUrl.append(getHost() + ":");
        mongoUrl.append(getPort());

        final ConnectionString connection = new ConnectionString(mongoUrl.toString());
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connection)
                .build();
        return MongoClients.create(settings);
    }
}
