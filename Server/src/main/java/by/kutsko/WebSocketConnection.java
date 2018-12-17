package by.kutsko;

import by.kutsko.model.Message;
import by.kutsko.service.WebSocketService;

import java.io.IOException;

public class WebSocketConnection extends AbstractConnection {

    private WebSocketService webSocketService;

    private boolean closed = false;

    public WebSocketConnection(String connectionUUID, WebSocketService webSocketService) {
        super(connectionUUID);
        this.webSocketService = webSocketService;
    }

    @Override
    public synchronized void send(Message message) throws IOException {
        webSocketService.send(getConnectionUUID(), message);
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized void setClosed(boolean closed) {
        this.closed = closed;
    }
}
