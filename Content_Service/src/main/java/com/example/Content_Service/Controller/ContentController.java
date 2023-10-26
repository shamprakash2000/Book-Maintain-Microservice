package com.example.Content_Service.Controller;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/addContent")
    public ResponseEntity<?> create(@RequestBody Content content) throws Exception{

        return contentService.insertContent(content);

    }

}
