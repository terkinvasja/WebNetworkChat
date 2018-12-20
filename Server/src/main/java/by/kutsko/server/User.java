package by.kutsko.server;

import by.kutsko.Connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private Connection connection;
    private final int numberOfChannels;
    private int freeChannels;
    private List<Companion> companions;

    public User(Connection connection, int numberOfChannels) {
        this.connection = connection;
        this.numberOfChannels = numberOfChannels;
        this.freeChannels = numberOfChannels;
        this.companions = new ArrayList<>(Collections.nCopies(numberOfChannels, null));
    }

    public Connection getConnection() {
        return connection;
    }

    public Companion getCompanionConnection(int channelId) {
        return companions.get(channelId);
    }

    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    public int getFreeChannels() {
        return freeChannels;
    }

    public void addCompanion(int channelId, Companion companion) {
        if (channelId < numberOfChannels && getCompanionConnection(channelId) == null) {
            companions.add(channelId, companion);
        }
    }

    public Companion getCompanionChannel() {
        int channel = companions.indexOf(null);
        if (channel != -1) {
            freeChannels--;
            return new Companion(connection, channel);
        }
        return null;
    }

    public void removeCompanion(String connectionUUID) {
        Companion companion;
        for (int i = 0; i < numberOfChannels; i++ ) {
            companion = companions.get(i);
            if (companion != null) {
                if (companion.getConnection().getConnectionUUID().equals(connectionUUID)) {
                    companions.add(i, null);
                    freeChannels++;
                    return;
                }
            }
        }
    }
}
