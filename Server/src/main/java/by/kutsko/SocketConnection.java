package by.kutsko;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import by.kutsko.model.Message;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class SocketConnection extends AbstractConnection {

    private static final Logger LOG = getLogger(SocketConnection.class);
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private volatile boolean isConnected = false;
    private String name;

    public SocketConnection(Socket socket, String connectionUUID) throws IOException {
        super(connectionUUID);
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public SocketConnection(Socket socket) throws IOException {
        this(socket, "");
    }

    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
            out.flush();
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        Message message;
        synchronized (in) {
            message = (Message) in.readObject();
            return message;
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            LOG.debug("", e);
        }
        try {
            out.close();
        } catch (IOException e) {
            LOG.debug("", e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            LOG.debug("", e);
        }
        isConnected = false;
        LOG.debug("Connection.close");
    }
}
