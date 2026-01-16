package com.example.backend.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ErrorLogWsPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishRefresh() {
        // refresh signal for error list
        messagingTemplate.convertAndSend("/topic/errors", "refresh");
    }
}
