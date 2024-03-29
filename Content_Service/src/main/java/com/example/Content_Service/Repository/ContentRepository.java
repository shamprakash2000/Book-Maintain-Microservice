package com.example.Content_Service.Repository;

import com.example.Content_Service.Model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface ContentRepository extends MongoRepository<Content, String> {
    boolean existsByTitle(String title);
    Content findByTitle(String tilte);

}
