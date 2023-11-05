package com.example.User_Interaction_Service.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ReadEvent {

    @Id
    private String id;

    private String contentId;

    private String userEmailId;

    private LocalDateTime readDate;


    public ReadEvent(String contentId, String userEmailId) {
        this.contentId = contentId;
        this.userEmailId = userEmailId;
    }

    public ReadEvent() {
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

    public LocalDateTime getReadDate() {
        return readDate;
    }

    public void setReadDate(LocalDateTime readDate) {
        this.readDate = readDate;
    }
}
