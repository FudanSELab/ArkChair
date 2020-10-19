package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class UserAndConference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userID;
    private Long conferenceID;
    private String topics;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Paper> papers = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "userAndConference")
    @JsonManagedReference
    private Set<Topic> nTopics = new HashSet<>();


    public UserAndConference(){}

    public UserAndConference(Long userID, Long conferenceID){
        this.conferenceID = conferenceID;
        this.userID = userID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getConferenceID() {
        return conferenceID;
    }

    public void setConferenceID(Long conferenceID) {
        this.conferenceID = conferenceID;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Paper> getPapers() {
        return papers;
    }

    public void setPapers(Set<Paper> papers) {
        this.papers = papers;
    }

    public Set<Topic> getNTopics() {
        return nTopics;
    }

    public void setNTopics(Set<Topic> nTopics) {
        this.nTopics = nTopics;
    }

    @Override
    public String toString() {
        return "User_Conference{" +
                "userID=" + userID + '\'' +
                ", conferenceID=" + conferenceID +
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
