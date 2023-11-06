package com.example.Content_Service.Controller;


import com.example.Content_Service.Model.Content;
import com.example.Content_Service.Service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("contentService/api")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.status(HttpStatus.OK).body("Content Service up and Running");
    }
    @PostMapping("/addContent")
    public ResponseEntity<?> create(@RequestBody Content content, HttpServletRequest request){
        return contentService.insertContent(content,request);
    }
    @GetMapping("/get/{contentId}")
    public ResponseEntity getContent(@PathVariable("contentId") String contentId){
       return contentService.getContent(contentId);

    }
    @PatchMapping("/updateStory/{contentId}")
    public ResponseEntity<?> updateStory(@RequestBody Content content,@PathVariable("contentId") String contentId,HttpServletRequest request){
        return contentService.updateStory(content,contentId,request);
    }
    @PatchMapping("/updateTitle/{contentId}")
    public ResponseEntity<?> updateTitle(@RequestBody Content content,@PathVariable("contentId") String contentId,HttpServletRequest request){
        return contentService.updateTitle(content,contentId,request);
    }
    @GetMapping("/deleteContent/{contentId}")
    public ResponseEntity deleteContent(@PathVariable("contentId") String contentId,HttpServletRequest request){
        return contentService.deleteContent(contentId,request);
    }
    @GetMapping("/getNewContent")
    public ResponseEntity getNewContent(){
        return contentService.getNewContent();
    }
    @PostMapping("/uploadCSV")
    public ResponseEntity<?> uploadDataFromCSVToDB(@RequestPart("file") MultipartFile file,HttpServletRequest request) {
        return contentService.uploadDataFromCSVToDB(file,request);
    }

    @GetMapping("/fetchTopContents")
    public ResponseEntity fetchTopContents(){
        return contentService.fetchTopContents();
    }
}
