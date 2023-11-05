package com.example.Content_Service.Service;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Repository.ContentRepository;
import com.example.Content_Service.Response.Response;
import com.example.Content_Service.Util.JwtUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<?> insertContent(Content content, HttpServletRequest request) {

        if (content.getStory()==null || content.getTitle()==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Content Fields are null. Please check the Content Object."));
        }

        if (content.getStory().isBlank() || content.getTitle().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Content Fields are empty. Please check the Content Object."));
        }

        if (contentRepository.existsByTitle(content.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("The given title already exists : "+content.getTitle()));

        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            content.setLastModifiedDate(localDateTime);
            content.setDateOfPublished(localDateTime);
            String emailId = extractEmailIdFromHTTPHeader(request);
            content.setuserEmailId(emailId);
            content.setIsUploadedByAdmin(false);
            try {
                Content savedContent = contentRepository.save(content);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Content inserted to database successfully", savedContent));
            } catch (Exception e) {
                e.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to insert the content to database.");
            }

        }

    }

    public String extractJWTFromHTTPHeader(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        String jwt = bearer.substring(7);
        return jwt;
    }

    public String extractEmailIdFromHTTPHeader(HttpServletRequest request) {
        String jwt = extractJWTFromHTTPHeader(request);
        String emailId = jwtUtil.extractAllClaims(jwt).get("emailId", String.class);
        return emailId;
    }
    public String extractRoleFromHTTPHeader(HttpServletRequest request) {
        String jwt = extractJWTFromHTTPHeader(request);
        String role = jwtUtil.extractAllClaims(jwt).get("role", String.class);
        return role;
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
                content.setuserEmailId(csvRecord.get("UserId"));
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

    public ResponseEntity getContent(String contentId) {
        Optional<Content> content= contentRepository.findById(contentId);

        if(!content.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",content));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response("Successfully fetched",content));

    }

    public ResponseEntity<?> updateStory(Content content,String contentId, HttpServletRequest request) {
        String JWT=extractJWTFromHTTPHeader(request);
        String emailId=extractEmailIdFromHTTPHeader(request);
        String role=extractRoleFromHTTPHeader(request);
        Optional<Content> fetchedContent= contentRepository.findById(contentId);
        if(!fetchedContent.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",fetchedContent));
        }
        Content tempContent=fetchedContent.get();
        if(role.equals("ADMIN")){
            if(!content.getStory().equals("") && !content.getStory().equals(null)){

                tempContent.setStory(content.getStory());
                tempContent.setLastModifiedDate(LocalDateTime.now());
                contentRepository.save(tempContent);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Story updated successfully",null));

            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Story is empty. Didnot update anything",null));
            }
        }
        else{
            if(tempContent.getuserEmailId().equals(emailId)){
                if(!content.getStory().equals("") && !content.getStory().equals(null)){
                    tempContent.setStory(content.getStory());
                    tempContent.setLastModifiedDate(LocalDateTime.now());
                    contentRepository.save(tempContent);
                    return ResponseEntity.status(HttpStatus.OK).body(new Response("Story updated successfully",null));

                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Story is empty. Didnot update anything",null));
                }

            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("The content is not belongs to this user, so unauthorized to modify.",null));
            }


        }


    }

    public ResponseEntity<?> updateTitle(Content content, String contentId, HttpServletRequest request) {
        String JWT=extractJWTFromHTTPHeader(request);
        String emailId=extractEmailIdFromHTTPHeader(request);
        String role=extractRoleFromHTTPHeader(request);
        Optional<Content> fetchedContent= contentRepository.findById(contentId);
        if(!fetchedContent.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",fetchedContent));
        }
        Content checkContentWithSameTitle=contentRepository.findByTitle(content.getTitle());

        if(checkContentWithSameTitle==null){
            Content tempContent=fetchedContent.get();
            if(role.equals("ADMIN")){
                if(!content.getTitle().equals("") && !content.getTitle().equals(null)){

                    tempContent.setTitle(content.getTitle());
                    tempContent.setLastModifiedDate(LocalDateTime.now());
                    contentRepository.save(tempContent);
                    return ResponseEntity.status(HttpStatus.OK).body(new Response("Title updated successfully",null));

                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Title is empty. Didnot update anything",null));
                }
            }
            else{
                if(tempContent.getuserEmailId().equals(emailId)){

                    if(!content.getTitle().equals("") && !content.getTitle().equals(null)){

                        tempContent.setTitle(content.getTitle());
                        tempContent.setLastModifiedDate(LocalDateTime.now());
                        contentRepository.save(tempContent);
                        return ResponseEntity.status(HttpStatus.OK).body(new Response("Title updated successfully",null));

                    }
                    else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Title is empty. Didnot update anything",null));
                    }

                }
                else{
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("The content is not belongs to this user, so unauthorized to modify.",null));
                }


            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("The given title already exists please change the title.",null));

        }
    }

    public ResponseEntity deleteContent(String contentId, HttpServletRequest request) {
        String JWT=extractJWTFromHTTPHeader(request);
        String emailId=extractEmailIdFromHTTPHeader(request);
        String role=extractRoleFromHTTPHeader(request);
        Optional<Content> fetchedContent= contentRepository.findById(contentId);
        if(!fetchedContent.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",fetchedContent));
        }
        Content tempContent=fetchedContent.get();
        if(tempContent.isContentDeleted()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("The content already deleted cannot perform delete operation again on deleted content.",null));
        }
        if(role.equals("ADMIN")){
            tempContent.setIsContentDeleted(true);
            tempContent.setLastModifiedDate(LocalDateTime.now());
            contentRepository.save(tempContent);
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Content deleted successfully",null));

        }
        else{
            if(tempContent.getuserEmailId().equals(emailId)){
                tempContent.setIsContentDeleted(true);
                tempContent.setLastModifiedDate(LocalDateTime.now());
                contentRepository.save(tempContent);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Content deleted successfully",null));

            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("The content is not belongs to this user, so unauthorized to delete the content.",null));
            }
        }


    }

    public long countNonDeletedContents() {
        Query countQuery = new Query(Criteria.where("isContentDeleted").is(false));
        return mongoTemplate.count(countQuery, Content.class, "content");
    }

    public Page<Content> getNonDeletedContentsPaged(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dateOfPublished")));
        Query query = new Query(Criteria.where("isContentDeleted").is(false));
        query.with(pageRequest);
        List<Content> contentList = mongoTemplate.find(query, Content.class, "content");
        return PageableExecutionUtils.getPage(contentList, pageRequest, () -> countNonDeletedContents());
    }

    public ResponseEntity getNewContent() {
        Page<Content> page=getNonDeletedContentsPaged(0,20);
        return ResponseEntity.status(HttpStatus.OK).body(new Response("Fetched new content successfully",page.getContent()));
    }
}

