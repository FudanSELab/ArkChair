package fudan.se.lab2.controller.request;

public class AuthorityAcceptedOrRejectedRequest {
    private Long conferenceId;
    private Long messageId;
    private String acceptOrRejected;
    private String[] topics;

    public Long getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        this.conferenceId = conferenceId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getAcceptOrRejected() {
        return acceptOrRejected;
    }

    public void setAcceptOrRejected(String acceptOrRejected) {
        this.acceptOrRejected = acceptOrRejected;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }
}
