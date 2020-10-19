package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;

    private String type;
    private int status;
    private String content;
    private Timestamp sentTime;//用于记录发信时间
    private String tag;
    private String tip;

    @ManyToOne()
    @JoinColumn
    @JsonIgnore
    private User user;

    public Message(){}

    public Message(String type, int status, String content){
        this.type = type;
        this.status = status;
        this.content = content;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp sentTime) {
        this.sentTime = sentTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString(){
        return "Message{" +
                "messageId=" + messageId + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", content='" + content +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(o != null)
            return this.toString().equals(o.toString());
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
}
