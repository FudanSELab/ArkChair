package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Conference{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //the time[0],time[1] from frontend is stored in startTime and endTime in backend respectively.
    private String nameAbbreviation;
    private String fullName;
    private Timestamp startTime;
    private Timestamp endTime;
    private String location;
    private Timestamp deadline;
    private Timestamp resultAnnounceDate;
    private String status;
    private String owner;
    private String topics;

    @ManyToMany(mappedBy = "conferences", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> users;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Topic> nTopics = new HashSet<>();

    public Conference(){
    }

    public Conference(String nameAbbreviation, String fullName, Timestamp startTime, Timestamp endTime, String location, Timestamp deadline,Timestamp resultAnnounceDate, String status, String owner, String topics){
        this.deadline = deadline;
        this.fullName = fullName;
        this.nameAbbreviation = nameAbbreviation;
        this.location = location;
        this.resultAnnounceDate = resultAnnounceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.owner = owner;
        this.topics = topics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAbbreviation() {
        return nameAbbreviation;
    }

    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public Timestamp getResultAnnounceDate() {
        return resultAnnounceDate;
    }

    public void setResultAnnounceDate(Timestamp resultAnnounceDate) {
        this.resultAnnounceDate = resultAnnounceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Set<Topic> getnTopics() {
        return nTopics;
    }

    public void setnTopics(Set<Topic> nTopics) {
        this.nTopics = nTopics;
    }

    @Override
    public String toString() {
        return "Conference{" +
                "id=" + id + '\'' +
                ", nameAbbreviation='" + nameAbbreviation + '\'' +
                ", fullName='" + fullName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                ", deadline=" + deadline + '\'' +
                ", resultAnnounceDate=" + resultAnnounceDate + '\'' +
                ", status=" + status + '\'' +
                ", owner=" + owner +
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
