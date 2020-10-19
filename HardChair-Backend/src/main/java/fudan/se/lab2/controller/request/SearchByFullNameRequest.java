package fudan.se.lab2.controller.request;

public class SearchByFullNameRequest {
    private String fullname;
    private Long conferenceId;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }
}
