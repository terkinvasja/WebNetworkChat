package by.kutsko;

import by.kutsko.model.Message;
import by.kutsko.model.MessageType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class Client {

    private ConsoleHelper consoleHelper;

    private Logger LOG = getLogger(Client.class);
    private final Pattern p = Pattern.compile("(/register) (agent|client) (\\w+)");
    private final String URL = "ws://localhost:8080/ws/";
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @Autowired
    public Client(ConsoleHelper consoleHelper) {
        this.consoleHelper = consoleHelper;
    }

    public void run() throws ExecutionException, InterruptedException {

        while (true) {
            consoleHelper.writeMessage("Зарегистрируйтесь");
            String message;
            if (!(message = consoleHelper.readString()).equals("/exit")) {
                Matcher m = p.matcher(message);
                if (m.matches()) {
                    String name = m.group(3);

                    //Создается объект класса Connection, используя сокет
                    LOG.debug("Create new WebSocket connection");
                    ListenableFuture<StompSession> f = connect();
                    StompSession stompSession = f.get();
                    LOG.info("Subscribing to greeting topic using session " + stompSession);
                    subscribeGreetings(stompSession);

                    LOG.debug("Client. Sending registration data");
                    if (m.group(2).equals("agent")) {
                        sendRegister(stompSession, new Message(MessageType.ADD_AGENT, name));
                    } else if (m.group(2).equals("client")) {
                        sendRegister(stompSession, new Message(MessageType.ADD_CLIENT, name));
                    }

                    while (true) {
                        if (!(message = consoleHelper.readString()).equals("/leave")) {
                            sendTextMessage(stompSession,name + ": " + message);
                        } else {
//                            history.clearHistory();
                            sendMessage(stompSession, new Message(MessageType.LEAVE));
                            stompSession.disconnect();
                            break;
                        }
                    }
                } else {
                    consoleHelper.writeMessage("Некорректно введена команада.");
                }
            } else {
                return;
            }
        }
    }

    private ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        return stompClient.connect(URL, headers, new MyHandler(), "localhost", 8080);
    }

    private void subscribeGreetings(StompSession stompSession) throws ExecutionException, InterruptedException {
        stompSession.subscribe("/user/topic/showResult", new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                Message message = Converter.toJavaObject(new String((byte[]) o));
                switch (message.getType()) {
                    case TEXT: {
                        consoleHelper.writeMessage(message.getData());
//                        history.addText(message.getData());
                        break;
                    }
                    case CHANGE_AGENT: {
                        consoleHelper.writeMessage(message.getData());
                        /*for (String text : history.getListHistory()) {
                            connection.send(new Message(MessageType.TEXT, text));
                        }*/
                        break;
                    }
                    case LEAVE: {
                        consoleHelper.writeMessage(message.getData());
                        return;
                    }
                    default: {
//                        throw new IOException("Unexpected MessageType");
                    }
                }
            }
        });
    }

    private void sendRegister(StompSession stompSession, Message message) {
        stompSession.send("/chatApp/add",
                Converter.toJSON(message).getBytes());
    }

    private void sendMessage(StompSession stompSession, Message message) {
        stompSession.send("/chatApp/sendConsole",
                Converter.toJSON(message).getBytes());
    }

    private void sendTextMessage(StompSession stompSession, String text) {
        sendMessage(stompSession, new Message(MessageType.TEXT, text));
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            LOG.info("Now connected");
        }
    }
}
