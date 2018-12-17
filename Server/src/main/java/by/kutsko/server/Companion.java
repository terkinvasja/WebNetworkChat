package by.kutsko.server;

import by.kutsko.Connection;

public class Companion {
    private Connection connection;
    private Integer channelId;

    public Companion(Connection connection, Integer channelId) {
        this.connection = connection;
        this.channelId = channelId;
    }

    public Connection getConnection() {
        return connection;
    }

    public Integer getChannelId() {
        return channelId;
    }
}
