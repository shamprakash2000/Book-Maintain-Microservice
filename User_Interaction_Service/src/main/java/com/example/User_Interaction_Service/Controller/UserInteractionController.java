package com.example.User_Interaction_Service.Controller;


import com.example.User_Interaction_Service.Model.LikeEvent;
import com.example.User_Interaction_Service.Model.ReadEvent;
import com.example.User_Interaction_Service.Service.UserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("userInteractionService/api")
public class UserInteractionController {


    @Autowired
    private UserInteractionService userInteractionService;
    @GetMapping("/health")
    public ResponseEntity<?> health() {

        return ResponseEntity.status(HttpStatus.OK).body("User Interaction Service Up and Running...!");
    }

    @PostMapping("/like")
    public ResponseEntity likeContent(@RequestBody LikeEvent likeEvent, HttpServletRequest request){
        return userInteractionService.likeContent(likeEvent,request);
    }

    @PatchMapping("/like/update")
    public ResponseEntity updateLikedContent(@RequestBody LikeEvent likeEvent, HttpServletRequest request){
        return userInteractionService.updateLikedContent(likeEvent,request);
    }

    @DeleteMapping("/like/delete")
    public ResponseEntity deleteLikedContent(@RequestBody LikeEvent likeEvent, HttpServletRequest request){
        return userInteractionService.deleteLikedContent(likeEvent,request);
    }

    @PostMapping("/read")
    public ResponseEntity readContent(@RequestBody ReadEvent readEvent, HttpServletRequest request){
        return userInteractionService.readContent(readEvent,request);
    }

    @PatchMapping("/read/update")
    public ResponseEntity updateReadContent(@RequestBody ReadEvent readEvent, HttpServletRequest request){
        return userInteractionService.updateReadContent(readEvent,request);
    }

    @DeleteMapping("/read/delete")
    public ResponseEntity deleteReadContent(@RequestBody ReadEvent readEvent, HttpServletRequest request){
        return userInteractionService.deleteReadContent(readEvent,request);
    }

    @GetMapping("/topContents")
    public List<String> fetchTopContents(){
        return userInteractionService.fetchTopContents();
    }




}
