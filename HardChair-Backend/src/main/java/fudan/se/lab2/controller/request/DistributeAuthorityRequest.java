package fudan.se.lab2.controller.request;

public class DistributeAuthorityRequest {
    private Long conferenceId;
    private Long[] users;

    public DistributeAuthorityRequest(Long conferenceId, Long[] users) {
        this.conferenceId = conferenceId;
        this.users = users;
    }

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Long[] getUsers() {
        return users;
    }

    public void setUsers(Long[] users) {
        this.users = users;
    }

}
