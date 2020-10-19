package fudan.se.lab2.controller.request;

public class VerifyRequest {
    private Long id;
    private boolean isAllowed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsAllowed() {
        return isAllowed;
    }
}
