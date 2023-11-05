package com.example.User_Interaction_Service.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class InteractionCount {

//    @Id
//    private String id;

    @Id
    private String contentId;

    private int count;


    public InteractionCount() {
    }

    public InteractionCount(String contentId, int count) {
        this.contentId = contentId;
        this.count = count;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
