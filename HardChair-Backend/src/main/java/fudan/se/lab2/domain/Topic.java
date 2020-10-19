package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String topic;
    private String tag;

    @ManyToMany(mappedBy = "nTopics")
    @JsonIgnore
    private Set<Conference> conferences;

    @ManyToOne()
    @JoinColumn
    @JsonIgnore
    private Paper paper;

    @ManyToOne()
    @JoinColumn
    @JsonIgnore
    private UserAndConference userAndConference;

    public Topic(){}

    public Topic(String topic){
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Set<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(Set<Conference> conferences) {
        this.conferences = conferences;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public UserAndConference getUserAndConference() {
        return userAndConference;
    }

    public void setUserAndConference(UserAndConference userAndConference) {
        this.userAndConference = userAndConference;
    }
}
