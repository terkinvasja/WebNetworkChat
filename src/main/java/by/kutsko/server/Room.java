package by.kutsko.server;

import by.kutsko.Connection;

public class Room {
    private Connection connection;
    private Connection companionConnection;
    private boolean agent;

    public Room(Connection connection, Connection companionConnection, boolean agent) {
        this.connection = connection;
        this.companionConnection = companionConnection;
        this.agent = agent;
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection getCompanionConnection() {
        return companionConnection;
    }

    public boolean isAgent() {
        return agent;
    }
}
