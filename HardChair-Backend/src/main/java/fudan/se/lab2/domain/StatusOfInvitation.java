package fudan.se.lab2.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class StatusOfInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Long conferenceId;
    Long userToSentId;
    String status;
    String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Long getUserToSentId() {
        return userToSentId;
    }

    public void setUserToSentId(Long userToSentId) {
        this.userToSentId = userToSentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "StatusOfInvitation{" +
                "conferenceId=" + conferenceId +
                ", userToSentId=" + userToSentId +
                ", status='" + status + '\'' +
                ", username='" + username + '\'' +
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
        return Objects.hash(id);
    }
}
