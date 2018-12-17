package by.kutsko.controllers;

import by.kutsko.WebSocketConnection;
import by.kutsko.model.Message;
import by.kutsko.model.MessageToUser;
import by.kutsko.model.MessageType;
import by.kutsko.server.Companion;
import by.kutsko.server.Room;
import by.kutsko.server.ServerCondition;
import by.kutsko.server.User;
import by.kutsko.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private ServerCondition serverCondition;
    @Autowired
    private WebSocketService webSocketService;

    @MessageMapping("/add")
    public void addClient(SimpMessageHeaderAccessor sha, MessageToUser messageToUser) throws Exception {

        Message message = messageToUser.getMessage();
        User user = new User(new WebSocketConnection(sha.getUser().getName(), webSocketService),
                Integer.parseInt(message.getData()));

        if (message.getType().equals(MessageType.ADD_AGENT)) {
            serverCondition.addAgent(user);
        } else if (message.getType().equals(MessageType.ADD_CLIENT)) {
            serverCondition.addClient(user);
        }

        serverCondition.getAgent();
    }

    @MessageMapping("/send")
    public void sendMessage(SimpMessageHeaderAccessor sha, MessageToUser messageToUser) throws Exception {

        String connectionUUID = sha.getUser().getName();
        User user = serverCondition.getUserMap().get(connectionUUID);
        Companion companion = user.getCompanionConnection(messageToUser.getChanelId());

        if (companion != null) {
            Message message = new Message(MessageType.TEXT, messageToUser.getMessage().getData());
            webSocketService.send(connectionUUID, new MessageToUser(messageToUser.getChanelId(), message));
            webSocketService.send(companion.getConnection().getConnectionUUID(),
                    new MessageToUser(companion.getChannelId(), message));
        } else {
            webSocketService.send(connectionUUID, "Server: Нет свободного агента. Пожалуйста подождите");
        }
    }

    @MessageMapping("/sendConsole")
    public void sendConsoleMessage(SimpMessageHeaderAccessor sha, MessageToUser messageToUser) throws Exception {

        String connectionUUID = sha.getUser().getName();
        User user = serverCondition.getUserMap().get(connectionUUID);
        Companion companion = user.getCompanionConnection(0);

        if (companion != null) {
            Message message = new Message(MessageType.TEXT, messageToUser.getMessage().getData());
            webSocketService.send(companion.getConnection().getConnectionUUID(),
                    new MessageToUser(0, message));
        } else {
            webSocketService.send(connectionUUID, "Server: Нет свободного агента. Пожалуйста подождите");
        }
    }

    @GetMapping("/")
    public String start() {
        return "start";
    }
}
