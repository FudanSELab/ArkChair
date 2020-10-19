package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Column(length = 3600)
    private String summary;

    private Long conferenceId;
    private Long userId;
    private String type;
    private int status;
    private String rebuttal;

    private String url;
    private Timestamp createdTime;

    private String topics;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "paper")
    @JsonManagedReference
    private Set<Author> authors = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "paper")
    @JsonManagedReference
    private Set<ReviewResult> reviewResults = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "paper")
    @JsonManagedReference
    private Set<Topic> nTopics = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "paper")
    @JsonManagedReference
    private Set<Post> posts = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "papers", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<UserAndConference> userAndConference;

    public Paper(){}

    public Paper(String title, String summary, Long conferenceId, Long userId, String type, String url, Timestamp createdTime){
        this.title = title;
        this.summary = summary;
        this.conferenceId = conferenceId;
        this.userId = userId;
        this.type = type;
        this.url = url;
        this.createdTime = createdTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getRebuttal() {
        return rebuttal;
    }

    public void setRebuttal(String rebuttal) {
        this.rebuttal = rebuttal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Set<ReviewResult> getReviewResults() {
        return reviewResults;
    }

    public void setReviewResults(Set<ReviewResult> reviewResults) {
        this.reviewResults = reviewResults;
    }

    public Set<Topic> getnTopics() {
        return nTopics;
    }

    public void setnTopics(Set<Topic> nTopics) {
        this.nTopics = nTopics;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<UserAndConference> getUserAndConference() {
        return userAndConference;
    }

    public void setUserAndConference(Set<UserAndConference> userAndConference) {
        this.userAndConference = userAndConference;
    }

    @Override
    public String toString() {
        return "Paper{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", conferenceId='" + conferenceId + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
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
