package fudan.se.lab2.controller.request;

public class SubmitRebuttalRequest {
    private Long paperId;
    private String rebuttalContent;

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getRebuttalContent() {
        return rebuttalContent;
    }

    public void setRebuttalContent(String rebuttalContent) {
        this.rebuttalContent = rebuttalContent;
    }
}
