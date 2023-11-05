package com.example.User_Interaction_Service.Service;


import com.example.User_Interaction_Service.Model.InteractionCount;
import com.example.User_Interaction_Service.Model.LikeEvent;
import com.example.User_Interaction_Service.Model.ReadEvent;
import com.example.User_Interaction_Service.Repository.InteractionCountRepository;
import com.example.User_Interaction_Service.Repository.LikeEventRepository;
import com.example.User_Interaction_Service.Repository.ReadEventRepository;
import com.example.User_Interaction_Service.Response.Response;
import com.example.User_Interaction_Service.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserInteractionService {

    @Autowired
    private LikeEventRepository likeEventRepository;

    @Autowired
    private ReadEventRepository readEventRepository;

    @Autowired
    private InteractionCountRepository interactionCountRepository;


    @Autowired
    private JwtUtil jwtUtil;

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

    public ResponseEntity likeContent(LikeEvent likeEvent, HttpServletRequest request) {
        String emailId = extractEmailIdFromHTTPHeader(request);
        String contentId=likeEvent.getContentId();
        Optional<LikeEvent> optionalLikeEvent=likeEventRepository.findByContentIdAndUserEmailId(contentId,emailId);
        if(!optionalLikeEvent.isPresent()){
            LikeEvent tempLikeEvent=new LikeEvent(contentId,emailId);
            tempLikeEvent.setLikedDate(LocalDateTime.now());
            LikeEvent savedLikeEvent=likeEventRepository.save(tempLikeEvent);
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Liked content successfully.", savedLikeEvent));
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Content already liked", null));
        }

    }

    public ResponseEntity readContent(ReadEvent readEvent, HttpServletRequest request) {
        String emailId = extractEmailIdFromHTTPHeader(request);
        String contentId=readEvent.getContentId();
        Optional<ReadEvent> optionalReadEvent=readEventRepository.findByContentIdAndUserEmailId(contentId,emailId);
        if(!optionalReadEvent.isPresent()){
            ReadEvent tempReadEvent=new ReadEvent(contentId,emailId);
            tempReadEvent.setReadDate(LocalDateTime.now());
            ReadEvent savedReadEvent=readEventRepository.save(tempReadEvent);
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Read content successfully.", savedReadEvent));
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Already this user read the content.", null));
        }
    }

    public ResponseEntity updateLikedContent(LikeEvent likeEvent, HttpServletRequest request) {
        return likeContent(likeEvent,request);
    }

    public ResponseEntity updateReadContent(ReadEvent readEvent, HttpServletRequest request) {
        return readContent(readEvent,request);
    }

    public ResponseEntity deleteLikedContent(LikeEvent likeEvent, HttpServletRequest request) {
        String emailId = extractEmailIdFromHTTPHeader(request);
        String contentId=likeEvent.getContentId();
        Optional<LikeEvent> optionalLikeEvent=likeEventRepository.findByContentIdAndUserEmailId(contentId,emailId);

        if(optionalLikeEvent.isPresent()){
            likeEventRepository.deleteByContentIdAndUserEmailId(contentId,emailId);
            return ResponseEntity.status(HttpStatus.OK).body(new Response("The liked content removed from liked event collection.", null));

        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("The content not present in liked event collection.", null));
        }
    }

    public ResponseEntity deleteReadContent(ReadEvent readEvent, HttpServletRequest request) {
        String emailId = extractEmailIdFromHTTPHeader(request);
        String contentId=readEvent.getContentId();
        Optional<ReadEvent> optionalReadEvent=readEventRepository.findByContentIdAndUserEmailId(contentId,emailId);

        if(optionalReadEvent.isPresent()){
            readEventRepository.deleteByContentIdAndUserEmailId(contentId,emailId);
            return ResponseEntity.status(HttpStatus.OK).body(new Response("The read content removed from read event collection.", null));

        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("The content not present in read event collection.", null));
        }


    }

    public List<String> fetchTopContents(){
        List<String> topContents = new ArrayList<>();
        List<InteractionCount> list=interactionCountRepository.findAllByOrderByCountDesc();

        for (InteractionCount interactionCount:list) {
            topContents.add(interactionCount.getContentId());
        }
        return topContents;
    }



}
