package com.example.User_Interaction_Service.Repository;

import com.example.User_Interaction_Service.Model.InteractionCount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InteractionCountRepository extends MongoRepository<InteractionCount, String> {

    InteractionCount findByContentId(String contentId);
    List<InteractionCount> findAllByOrderByCountDesc();
}
