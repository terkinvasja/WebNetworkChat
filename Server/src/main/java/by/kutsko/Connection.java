package by.kutsko;

import by.kutsko.model.Message;

import java.io.IOException;

public interface Connection {

    void send(Message message) throws IOException;

    boolean isClosed();

    void setClosed(boolean closed);

    String getConnectionUUID();
}
