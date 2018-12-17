package by.kutsko;

public abstract class AbstractConnection implements Connection {

    private String connectionUUID;

    public AbstractConnection(String connectionUUID) {
        this.connectionUUID = connectionUUID;
    }

    @Override
    public String getConnectionUUID() {
        return connectionUUID;
    }
}
