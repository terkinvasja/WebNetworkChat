package by.kutsko.controllers;

import by.kutsko.WebSocketConnection;
import by.kutsko.model.Message;
import by.kutsko.model.MessageType;
import by.kutsko.server.Room;
import by.kutsko.server.ServerCondition;
import by.kutsko.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ServerCondition serverCondition;
    @Autowired
    private WebSocketService webSocketService;

    @MessageMapping("/add")
    public void addClient(SimpMessageHeaderAccessor sha, Message message) throws Exception {

        WebSocketConnection webSocketConnection = new WebSocketConnection(sha.getUser().getName(), webSocketService);
        webSocketConnection.setName(message.getData());

        if (message.getType().equals(MessageType.ADD_AGENT)) {
            serverCondition.addAgent(webSocketConnection);
        } else if (message.getType().equals(MessageType.ADD_CLIENT)) {
            serverCondition.addClient(webSocketConnection);
        }

        serverCondition.getAgent();
    }

    @MessageMapping("/send")
    public void sendMessage(SimpMessageHeaderAccessor sha, Message message) throws Exception {

        String connectionUUID = sha.getUser().getName();

        if (serverCondition.getRooms().containsKey(connectionUUID)) {
            Room room = serverCondition.getRooms().get(connectionUUID);
            room.getConnection().send(new Message(MessageType.TEXT, message.getData()));
            room.getCompanionConnection().send(new Message(MessageType.TEXT, message.getData()));
        } else {
            webSocketService.send(connectionUUID, "Server: Нет свободного агента. Пожалуйста подождите");
        }
    }

    @RequestMapping("/")
    public String start() {
        return "start";
    }
}
