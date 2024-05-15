package esgi.codelink.entity;

import java.io.Serializable;

public class MsgId implements Serializable {

    private User sender;

    private User receiver;

    public MsgId() {}

    public MsgId(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
