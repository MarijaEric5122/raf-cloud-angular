package com.example.backend_MarijaNatasa.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ErrorLogWsPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishRefresh() {
        // WS refresh signal za listu gresaka na frontu
        messagingTemplate.convertAndSend("/topic/errors", "refresh");
    }
}
