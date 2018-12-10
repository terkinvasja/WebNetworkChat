package by.kutsko;

public abstract class AbstractConnection implements Connection {

    private String connectionUUID;
    private String name;

    public AbstractConnection(String connectionUUID) {
        this.connectionUUID = connectionUUID;
    }

    @Override
    public String getConnectionUUID() {
        return connectionUUID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
