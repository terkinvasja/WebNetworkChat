package by.kutsko;

import by.kutsko.model.Message;

import java.io.IOException;

public interface Connection {

    public void send(Message message) throws IOException;

    public boolean isClosed();


    public String getConnectionUUID();

    public String getName();

    public void setName(String name);
}
