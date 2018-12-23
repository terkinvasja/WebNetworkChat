package by.kutsko.server;

import by.kutsko.model.Message;
import by.kutsko.model.MessageToUser;
import by.kutsko.model.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import by.kutsko.service.WebSocketService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class ServerCondition {
    private final Logger LOG = getLogger(ServerCondition.class);

    @Autowired
    private WebSocketService webSocketService;

    private final LinkedList<User> agentList = new LinkedList<>();
    private final LinkedList<User> clientList = new LinkedList<>();
    private final HashMap<String, User> userMap = new HashMap<>();
    private final HashMap<String, Room> rooms = new HashMap<>();

    public void addAgent(User user) {
        synchronized (agentList) {
            agentList.add(user);
            addUser(user);
        }
    }

    public void addClient(User user) {
        synchronized (clientList) {
            clientList.add(user);
            addUser(user);
        }
    }

    private void addUser(User user) {
        synchronized (userMap) {
            userMap.put(user.getConnection().getConnectionUUID(), user);
        }
    }

    public synchronized void getAgent() {
        User agent;
        User client;

        agent = searchValidConnection(agentList);
        client = searchValidConnection(clientList);

        if ((agent != null) && (client != null)) {
            Companion agentFreeChanel = agent.getCompanionChannel();
            Companion clientFreeChanel = client.getCompanionChannel();
            agent.addCompanion(agentFreeChanel.getChannelId(), clientFreeChanel);
            client.addCompanion(clientFreeChanel.getChannelId(), agentFreeChanel);
            LOG.debug(String.format("%s and %s start chat.",
                    agent.getConnection().getConnectionUUID(), client.getConnection().getConnectionUUID()));

            webSocketService.send(agent.getConnection().getConnectionUUID(),
                    new MessageToUser(agentFreeChanel.getChannelId(),
                            new Message(MessageType.TEXT, "Server: Клиент присоеденился к чату")));
            webSocketService.send(client.getConnection().getConnectionUUID(),
                    new MessageToUser(clientFreeChanel.getChannelId(),
                            new Message(MessageType.CHANGE_AGENT, "Server: Агент присоеденился к чату")));
        } else if (agent != null) {
            LOG.debug(String.format("Agent %s return to list.", agent.getConnection().getConnectionUUID()));
            agentList.addFirst(agent);
        } else if (client != null) {
            LOG.debug(String.format("Client %s return to list.", client.getConnection().getConnectionUUID()));
            clientList.addFirst(client);
        }
        LOG.debug(String.format("Server.getAgent clientList=%s, agentList=%s",
                clientList.size(), agentList.size()));
    }
    //TODO
/*    public synchronized void returnAgent(String clientConnectionUUID) {
        LOG.debug("Server.returnAgent");
        Room room = rooms.get(clientConnectionUUID);
        Connection agentConnection = room.getCompanionConnection();
        LOG.debug(String.format("Client %s end chat. Agent %s return to queue.",
                clientConnectionUUID, agentConnection.getConnectionUUID()));
        try {
            agentConnection.send(new Message(MessageType.TEXT, "Server: Клиент закончил чат"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        agentList.add(agentConnection);
        rooms.remove(clientConnectionUUID);
        rooms.remove(agentConnection.getConnectionUUID());
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
        clientList.addFirst(clientConnection);
        rooms.remove(agentConnectionUUID);
        rooms.remove(clientConnection.getConnectionUUID());
        getAgent();
    }*/

    public synchronized void disconnect(String connectionUUID) {
        User user = userMap.get(connectionUUID);
        if (user != null) {
            for (Companion companion : user.disconnect()) {
                webSocketService.send(companion.getConnection().getConnectionUUID(),
                        new MessageToUser(companion.getChannelId(),
                                new Message(MessageType.TEXT, "Companion disconnected.")));
            }
        }
/*        Connection connection = connectionHashMap.get(connectionUUID);
        if (connection != null) {
            connection.setClosed(true);
        }
        Room room = rooms.get(connectionUUID);
        if (room != null) {
            if (room.isAgent()) {
                reGetAgent(room.getConnection().getConnectionUUID());
            } else {
                returnAgent(room.getConnection().getConnectionUUID());
            }
        }*/
    }

    public synchronized void deleteUUID(String connectionUUID) {
        rooms.remove(connectionUUID);
        userMap.remove(connectionUUID);
        LOG.debug(String.format("Server.deleteUUID clientList=%s, agentList=%s",
                clientList.size(), agentList.size()));
    }

    private User searchValidConnection(LinkedList<User> userList) {
        User user = null;
        while (!userList.isEmpty()) {
            user = userList.poll();
//            if (connection == null) break;
            if (!user.getConnection().isClosed()) {
                LOG.debug(String.format("searchValidConnection. %s is connected.",
                        user.getConnection().getConnectionUUID()));
                break;
            } else {
                deleteUUID(user.getConnection().getConnectionUUID());
                LOG.debug(String.format("searchValidConnection. %s is closed.",
                        user.getConnection().getConnectionUUID()));
                user = null;
            }
        }
        return user;
    }

    public HashMap<String, User> getUserMap() {
        return userMap;
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
