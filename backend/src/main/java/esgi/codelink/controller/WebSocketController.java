package esgi.codelink.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPipelineUpdate(String message) {
        messagingTemplate.convertAndSend("/topic/pipelineUpdates", message);
    }
}
