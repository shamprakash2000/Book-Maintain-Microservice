package com.example.User_Service.Configuration;

import com.example.User_Service.Model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import javax.annotation.PostConstruct;


@Configuration
public class MongoConfiguration {

    private final MongoTemplate mongoTemplate;

    public MongoConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void createUniqueIndex() {


        IndexOperations indexOps = mongoTemplate.indexOps(User.class);

        // Create a unique index on the mobileNumber field in ascending order
        Index emailIndex = new Index().on("emailId", Sort.Direction.ASC).unique();
        Index phoneIndex = new Index().on("phoneNumber", Sort.Direction.ASC).unique();
        indexOps.ensureIndex(emailIndex);
        indexOps.ensureIndex(phoneIndex);

    }
}