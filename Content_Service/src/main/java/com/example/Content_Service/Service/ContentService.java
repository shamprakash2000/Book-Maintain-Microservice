package com.example.Content_Service.Service;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Repository.ContentRepository;
import com.example.Content_Service.Response.Response;
import com.example.Content_Service.Util.JwtUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

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

    public ResponseEntity<?> uploadDataFromCSVToDB(MultipartFile file,HttpServletRequest request) {
        String JWT=extractJWTFromHTTPHeader(request);
        String emailId=extractEmailIdFromHTTPHeader(request);
        String role=extractRoleFromHTTPHeader(request);
        if(role.equals("ADMIN")){
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(" The sent file is empty",null));
            }
            try {
                Resource resource = new ClassPathResource(".");
                String uploadsPath = resource.getFile().getAbsolutePath() + File.separator ;

                File uploadedFile = new File(uploadsPath, "content_file.csv");
                file.transferTo(uploadedFile);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(" Unable to store the file in the location",null));
            }
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            try {
                jobLauncher.run(job, jobParameters);
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(" Unable complete the Job, Unable to add contents to databse.",null));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new Response(" Successfully added the contents from csv to database.",null));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("Users not allowed to upload CSV, only Admins can upload csv data.",null));
        }
    }

    public ResponseEntity getContent(String contentId) {
        Optional<Content> content= contentRepository.findById(contentId);

        if(!content.isPresent() ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",null));
        }

        if(content.get().isContentDeleted()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Couldnot find requested content.",null));
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

    public ResponseEntity fetchTopContents() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.exchange(
                "http://localhost:8083/userInteractionService/api/topContents",
                HttpMethod.GET, null, List.class);

        List<String> list=response.getBody();
        List<Content> contentList=new ArrayList<>();
        for (String contentId:list) {
           Optional<Content> tempContent= contentRepository.findById(contentId);
           contentList.add(tempContent.get());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response("Fetched top content successfully",contentList));

    }
}

