package com.example.Content_Service.Service;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Repository.ContentRepository;
import com.example.Content_Service.Response.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    public ResponseEntity<?> insertContent(Content content) {

        if (content.getUserId().isBlank() || content.getStory().isBlank() || content.getTitle().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Content Fields are empty. Please check the Content Object."));
        }

        if (contentRepository.existsByTitle(content.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("The given title already exists : "+content.getTitle()));

        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            content.setLastModifiedDate(localDateTime);
            content.setDateOfPublished(localDateTime);
            try {
                Content savedContent = contentRepository.save(content);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Content inserted to database successfully", savedContent));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to insert the content to database.");
            }

        }

    }

    public ResponseEntity<?> insertContentFromCSV(MultipartFile file) {

        try  {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            for (CSVRecord csvRecord : csvParser) {

                //System.out.println(csvRecord.get("name") + " : " + csvRecord.get("age"));
                System.out.println(csvRecord.get("Title")+" "+csvRecord.get("Story")+" "+csvRecord.get("UserId"));
                Content content = new Content();
                content.setStory(csvRecord.get("Story"));
                content.setTitle(csvRecord.get("Title"));
                content.setUserId(csvRecord.get("UserId"));
                LocalDateTime localDateTime = LocalDateTime.now();
                content.setLastModifiedDate(localDateTime);
                content.setDateOfPublished(localDateTime);

                contentRepository.save(content);


            }

            return ResponseEntity.ok("Data ingested successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to ingest data from the CSV file.");
        } catch (Exception e) {

        }


        return null;
    }

}

