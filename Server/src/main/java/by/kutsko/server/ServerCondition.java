package by.kutsko.server;

import by.kutsko.Connection;
import by.kutsko.model.Message;
import by.kutsko.model.MessageType;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class ServerCondition {
    private final Logger LOG = getLogger(ServerCondition.class);

    private final LinkedList<Connection> agentList = new LinkedList<>();
    private final LinkedList<Connection> clientList = new LinkedList<>();
    private final HashMap<String, Connection> connectionHashMap = new HashMap<>();
    private final HashMap<String, Room> rooms = new HashMap<>();

    public void addAgent(Connection connection) {
        synchronized (agentList) {
            agentList.add(connection);
            addConnection(connection);
        }
    }

    public void addClient(Connection connection) {
        synchronized (clientList) {
            clientList.add(connection);
            addConnection(connection);
        }
    }

    private void addConnection(Connection connection) {
        synchronized (connectionHashMap) {
            connectionHashMap.put(connection.getConnectionUUID(), connection);
        }
    }

    public synchronized void getAgent() {
        Connection agentConnection;
        Connection clientConnection;

        agentConnection = searchValidConnection(agentList);
        clientConnection = searchValidConnection(clientList);

        if ((agentConnection != null) && (clientConnection != null)) {
            rooms.put(agentConnection.getConnectionUUID(), new Room(agentConnection, clientConnection, true));
            rooms.put(clientConnection.getConnectionUUID(), new Room(clientConnection, agentConnection, false));
            LOG.debug(String.format("%s and %s start chat.",
                    agentConnection.getConnectionUUID(), clientConnection.getConnectionUUID()));
            try {
                agentConnection.send(new Message(MessageType.TEXT,
                        String.format("Server: Клиент %s присоеденился к чату", clientConnection.getName())));
                clientConnection.send(new Message(MessageType.CHANGE_AGENT,
                        "Server: Ваш агент " + agentConnection.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (agentConnection != null) {
            LOG.debug(String.format("Agent %s return to list.", agentConnection.getConnectionUUID()));
            agentList.addFirst(agentConnection);
        } else if (clientConnection != null) {
            LOG.debug(String.format("Client %s return to list.", clientConnection.getConnectionUUID()));
            clientList.addFirst(clientConnection);
        }
        LOG.debug(String.format("Server.getAgent clientList=%s, agentList=%s",
                clientList.size(), agentList.size()));
    }

    public synchronized void returnAgent(String clientConnectionUUID) {
        LOG.debug("Server.returnAgent");
        Connection agentConnection = rooms.get(clientConnectionUUID).getCompanionConnection();
        LOG.debug(String.format("Client %s end chat. Agent %s return to queue.",
                clientConnectionUUID, agentConnection.getConnectionUUID()));
        agentList.add(agentConnection);
        String clientName = connectionHashMap.get(clientConnectionUUID).getName();
        connectionHashMap.remove(clientConnectionUUID);
        rooms.remove(clientConnectionUUID);
        rooms.remove(agentConnection.getConnectionUUID());
        try {
            agentConnection.send(new Message(MessageType.TEXT,
                    String.format("Server: Клиент %s закончил чат", clientName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getAgent();
    }

    public synchronized void reGetAgent(String agentConnectionUUID) {
        Connection clientConnection = rooms.get(agentConnectionUUID)
                .getCompanionConnection();
        try {
            clientConnection.send(new Message(MessageType.TEXT,
                    "Server: Агент разорвал соединение. Подождите пока подключится новый агент."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rooms.remove(clientConnection.getConnectionUUID());
        rooms.remove(agentConnectionUUID);
        connectionHashMap.remove(agentConnectionUUID);
        clientList.addFirst(connectionHashMap.get(clientConnection.getConnectionUUID()));
        getAgent();
    }

    public synchronized void disconnect(String connectionUUID) {
        Room room = rooms.get(connectionUUID);
        if (room != null) {
            if (room.isAgent()) {
                reGetAgent(room.getConnection().getConnectionUUID());
            } else {
                returnAgent(room.getConnection().getConnectionUUID());
            }
        }
    }

    public synchronized void deleteUUID(String connectionUUID) {
        rooms.remove(connectionUUID);
        connectionHashMap.remove(connectionUUID);
        LOG.debug(String.format("Server.deleteUUID clientList=%s, agentList=%s",
                clientList.size(), agentList.size()));
    }

    private Connection searchValidConnection(LinkedList<Connection> linkedList) {
        Connection connection = null;
        while (!linkedList.isEmpty()) {
            connection = linkedList.poll();
//            if (connection == null) break;
            if (!connection.isClosed()) {
                LOG.debug(String.format("searchValidConnection. %s is connected.", connection.getConnectionUUID()));
                break;
            } else {
                connectionHashMap.remove(connection.getConnectionUUID());
                LOG.debug(String.format("searchValidConnection. %s is closed.", connection.getConnectionUUID()));
                connection = null;
            }
        }
        return connection;
    }

    public HashMap<String, Room> getRooms() {
        synchronized (rooms) {
            return rooms;
        }
    }

    public int getSizeAgentList() {
        return agentList.size();
    }

    public int getSizeClientList() {
        return clientList.size();
    }

    public synchronized void clearAll() {
        rooms.clear();
        agentList.clear();
        clientList.clear();
    }

}
