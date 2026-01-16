package com.example.backend.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScheduleWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ScheduleWsPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishRefresh() {
        // refresh signal for schedule list
        messagingTemplate.convertAndSend("/topic/schedules", "refresh");
    }
}
