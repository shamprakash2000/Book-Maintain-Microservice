package com.example.User_Interaction_Service.Configuration;

import com.example.User_Interaction_Service.Model.LikeEvent;
import com.example.User_Interaction_Service.Model.ReadEvent;
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
        IndexOperations likeEventIndexOps = mongoTemplate.indexOps(LikeEvent.class);
        Index likeIndex = new Index().on("contentId", Sort.Direction.ASC).on("userEmailId", Sort.Direction.ASC).unique();
        likeEventIndexOps.ensureIndex(likeIndex);
        IndexOperations readEventIndexOps = mongoTemplate.indexOps(ReadEvent.class);
        Index readIndex = new Index().on("contentId", Sort.Direction.ASC).on("userEmailId", Sort.Direction.ASC).unique();
        readEventIndexOps.ensureIndex(readIndex);
    }


}