package com.example.User_Interaction_Service.Repository;

import com.example.User_Interaction_Service.Model.LikeEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikeEventRepository  extends MongoRepository<LikeEvent, String>{


    Optional<LikeEvent> findByContentIdAndUserEmailId(String contentId,String emailId);
    void deleteByContentIdAndUserEmailId(String contentId,String emailId);

}



