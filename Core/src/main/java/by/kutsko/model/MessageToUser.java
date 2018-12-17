package by.kutsko.model;

public class MessageToUser {
    private Integer chanelId;
    private Message message;

    public MessageToUser() {
        this(0, new Message());
    }

    public MessageToUser(int chanelId, Message message) {
        this.chanelId = chanelId;
        this.message = message;
    }

    public Integer getChanelId() {
        return chanelId;
    }

    public Message getMessage() {
        return message;
    }
}
