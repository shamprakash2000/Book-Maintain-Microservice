package com.example.User_Interaction_Service.Repository;


import com.example.User_Interaction_Service.Model.ReadEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReadEventRepository extends MongoRepository<ReadEvent, String> {

    Optional<ReadEvent> findByContentIdAndUserEmailId(String contentId, String emailId);
    void deleteByContentIdAndUserEmailId(String contentId,String emailId);
}
