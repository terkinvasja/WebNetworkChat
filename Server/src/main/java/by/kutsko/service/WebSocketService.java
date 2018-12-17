package by.kutsko.service;

import by.kutsko.model.Message;
import by.kutsko.model.MessageToUser;
import by.kutsko.model.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final String destination = "/topic/showResult";

    @Autowired
    private SimpMessagingTemplate template;

    public void send(String UUID, String message) {
        send(UUID, new Message(MessageType.TEXT, message));
    }

    public void send(String UUID, Message message) {
        send(UUID, new MessageToUser(0, message));
    }

    public void send(String UUID, MessageToUser msgToUser) {
        template.convertAndSendToUser(UUID, destination, msgToUser);
    }
}
