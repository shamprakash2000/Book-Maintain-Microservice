package com.example.User_Interaction_Service.Service;


import com.example.User_Interaction_Service.Model.InteractionCount;
import com.example.User_Interaction_Service.Model.LikeEvent;
import com.example.User_Interaction_Service.Model.ReadEvent;
import com.example.User_Interaction_Service.Repository.InteractionCountRepository;
import com.example.User_Interaction_Service.Repository.LikeEventRepository;
import com.example.User_Interaction_Service.Repository.ReadEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InteractionCountScheduledTask {

    @Autowired
    private LikeEventRepository likeEventRepository;

    @Autowired
    private ReadEventRepository readEventRepository;

    @Autowired
    private InteractionCountRepository interactionCountRepository;

    // Run this method every 3 minutes
    @Scheduled(fixedRate = 1000 * 60 * 3)
    public void runScheduledTask() {
        System.out.println("Scheduled task executed at: " + System.currentTimeMillis());
        List<LikeEvent> likeEvents = likeEventRepository.findAll();

        List<ReadEvent> readEvents = readEventRepository.findAll();

        Map<String, Integer> contentInteractions = new HashMap<>();

        for (LikeEvent likeEvent : likeEvents) {
            String contentId = likeEvent.getContentId();
            contentInteractions.put(contentId, contentInteractions.getOrDefault(contentId, 0) + 1);
        }

        for (ReadEvent readEvent : readEvents) {
            String contentId = readEvent.getContentId();
            contentInteractions.put(contentId, contentInteractions.getOrDefault(contentId, 0) + 1);
        }
        Map<String, Integer> sortedMap = contentInteractions.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            InteractionCount interactionCount = new InteractionCount(entry.getKey(), entry.getValue());
            interactionCountRepository.save(interactionCount);


        }
    }
}
