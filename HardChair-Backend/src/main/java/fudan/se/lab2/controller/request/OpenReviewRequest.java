package fudan.se.lab2.controller.request;

public class OpenReviewRequest {
    private Long conferenceId;
    private int strategy;

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public int getStrategy() {
        return strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }
}
