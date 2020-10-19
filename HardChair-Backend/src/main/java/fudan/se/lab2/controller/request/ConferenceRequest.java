package fudan.se.lab2.controller.request;

public class ConferenceRequest {

    private String nameAbbreviation;
    private String fullName;
    private String[] time;
    private String location;
    private String deadline;
    private String resultAnnounceDate;
    private String[] topic;

    public ConferenceRequest(){
        // no codes here
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

    public String[] getTime() {
        return time;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getResultAnnounceDate() {
        return resultAnnounceDate;
    }

    public void setResultAnnounceDate(String resultAnnounceDate) {
        this.resultAnnounceDate = resultAnnounceDate;
    }

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }
}
