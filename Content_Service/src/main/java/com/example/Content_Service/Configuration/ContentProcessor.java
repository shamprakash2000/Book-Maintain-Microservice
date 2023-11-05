package com.example.Content_Service.Configuration;

import com.example.Content_Service.Model.Content;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

public class ContentProcessor implements ItemProcessor<Content,Content> {
    @Override
    public Content process(Content content) throws Exception {

        if(content.getTitle().equals("") || content.getStory().equals("") || content.getuserEmailId().equals("")){
            return null;
        }
        if(content.getTitle().equals(null) || content.getStory().equals(null) || content.getuserEmailId().equals(null)){
            return null;
        }

        content.setDateOfPublished(LocalDateTime.now());
        content.setLastModifiedDate(LocalDateTime.now());
        content.setIsUploadedByAdmin(true);

        return content;
    }
}
