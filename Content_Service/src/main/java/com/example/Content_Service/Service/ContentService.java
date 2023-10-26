package com.example.Content_Service.Service;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Repository.ContentRepository;
import com.example.Content_Service.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    public ResponseEntity<?> insertContent(Content content){

        if(content.getUserId().isBlank() || content.getStory().isBlank() || content.getTitle().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Content Fields are empty. Please check the Content Object."));
        }

        if(contentRepository.existsByTitle(content.getTitle())){
            System.out.println("exist this title");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Title already exists"));

        }else{
            LocalDateTime localDateTime=LocalDateTime.now();
            content.setLastModifiedDate(localDateTime);
            content.setDateOfPublished(localDateTime);
            try{
                Content savedContent = contentRepository.save(content);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Content inserted to database successfully",savedContent));
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to insert the content to database.");
            }

        }

    }
}

