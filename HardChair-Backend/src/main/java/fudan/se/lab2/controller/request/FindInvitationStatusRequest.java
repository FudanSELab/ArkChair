package fudan.se.lab2.controller.request;

public class FindInvitationStatusRequest {
    private Long conferenceId;

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }
}
