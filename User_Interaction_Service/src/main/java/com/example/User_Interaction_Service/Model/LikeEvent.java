package com.example.User_Interaction_Service.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class LikeEvent {
    @Id
    private String id;

    private String contentId;

    private String userEmailId;

    private LocalDateTime likedDate;

    public LikeEvent(String contentId, String userEmailId) {
        this.contentId = contentId;
        this.userEmailId = userEmailId;
    }

    public LikeEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public LocalDateTime getLikedDate() {
        return likedDate;
    }

    public void setLikedDate(LocalDateTime likedDate) {
        this.likedDate = likedDate;
    }
}
