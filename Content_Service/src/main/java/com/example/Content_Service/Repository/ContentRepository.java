package com.example.Content_Service.Repository;

import com.example.Content_Service.Model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContentRepository extends MongoRepository<Content, String> {
    boolean existsByTitle(String title);
    Optional<Content> findById(String contentId);

    Content findByTitle(String tilte);

}
