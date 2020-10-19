package fudan.se.lab2.controller.request;

public class ReviseOrConfirmReviewResultRequest {
    private Long reviewResultId;
    private int score;
    private String comment;
    private String confidence;

    public Long getReviewResultId() {
        return reviewResultId;
    }

    public void setReviewResultId(Long reviewResultId) {
        this.reviewResultId = reviewResultId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

}
