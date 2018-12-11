package by.kutsko.service;

import by.kutsko.model.Message;
import by.kutsko.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void send(String connectionUUID, String message) {
        template.convertAndSendToUser(connectionUUID,
                "/topic/showResult",
                new Message(MessageType.TEXT, message));
    }
}
