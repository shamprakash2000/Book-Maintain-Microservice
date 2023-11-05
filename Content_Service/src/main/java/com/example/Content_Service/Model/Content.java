package com.example.Content_Service.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.Date;

@Document
public class Content {
    @Id
    private String id;
    private String title;
    private String story;
    private LocalDateTime dateOfPublished;
    private String userEmailId;

    private LocalDateTime lastModifiedDate;

    private boolean isUploadedByAdmin;

    private boolean isContentDeleted;


    public boolean isContentDeleted() {
        return isContentDeleted;
    }

    public void setIsContentDeleted(boolean contentDeleted) {
        isContentDeleted = contentDeleted;
    }

    public boolean isUploadedByAdmin() {
        return isUploadedByAdmin;
    }

    public void setIsUploadedByAdmin(boolean uploadedByAdmin) {
        isUploadedByAdmin = uploadedByAdmin;
    }

// Constructors, getters, and setters

    public Content() {
    }

    public Content(String id, String title, String story, LocalDateTime dateOfPublished, String userEmailId, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.title = title;
        this.story = story;
        this.dateOfPublished = dateOfPublished;
        this.userEmailId = userEmailId;
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public LocalDateTime getDateOfPublished() {
        return dateOfPublished;
    }

    public void setDateOfPublished(LocalDateTime dateOfPublished) {
        this.dateOfPublished = dateOfPublished;
    }

    public String getuserEmailId() {
        return userEmailId;
    }

    public void setuserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
