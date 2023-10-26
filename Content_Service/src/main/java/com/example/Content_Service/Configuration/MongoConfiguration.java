package com.example.Content_Service.Configuration;


import com.example.Content_Service.Model.Content;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDirection;
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


        IndexOperations indexOps = mongoTemplate.indexOps(Content.class);

        // Create a unique index on the mobileNumber field in ascending order
        Index index = new Index().on("title", Sort.Direction.ASC).unique();
        indexOps.ensureIndex(index);
//        index=new Index().on("story",Sort.Direction.ASC).unique();
//        indexOps.ensureIndex(index);
    }
}