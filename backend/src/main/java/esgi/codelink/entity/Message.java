package esgi.codelink.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
@Entity
@IdClass(MsgId.class)
public class Message {

    @Id
    @ManyToOne
    private User sender;

    @Id
    @ManyToOne
    private User receiver;

    private String msg;
    private long timestamp;

    public Message() {}

    public Message(User sender, User receiver, String msg, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.timestamp = timestamp;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", msg='" + msg + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}