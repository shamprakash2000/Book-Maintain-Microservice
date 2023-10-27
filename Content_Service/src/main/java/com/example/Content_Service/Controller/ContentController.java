package com.example.Content_Service.Controller;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Service.ContentService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
    public ResponseEntity<?> create(@RequestBody Content content){

        return contentService.insertContent(content);

    }

    @PostMapping("/uploadCSV")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
       return contentService.insertContentFromCSV(file);
    }


}
