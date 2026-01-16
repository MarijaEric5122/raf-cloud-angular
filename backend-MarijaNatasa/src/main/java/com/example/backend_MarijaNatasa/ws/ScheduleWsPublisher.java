package com.example.backend_MarijaNatasa.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScheduleWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ScheduleWsPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishRefresh() {
        // WS refresh signal za listu schedulera na frontu
        messagingTemplate.convertAndSend("/topic/schedules", "refresh");
    }
}
