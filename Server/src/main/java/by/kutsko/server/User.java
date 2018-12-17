package by.kutsko.server;

import by.kutsko.Connection;

import java.util.HashMap;

public class User {
    private Connection connection;
    private final int numberOfChannels;
    private HashMap<Integer,Companion> companions = new HashMap<>();

    public User(Connection connection, int numberOfChannels) {
        this.connection = connection;
        this.numberOfChannels = numberOfChannels;
    }

    public Connection getConnection() {
        return connection;
    }

    public Companion getCompanionConnection(Integer channelId) {
        return companions.get(channelId);
    }

    public int getNumberOfChannels() {
        return numberOfChannels;
    }
}
