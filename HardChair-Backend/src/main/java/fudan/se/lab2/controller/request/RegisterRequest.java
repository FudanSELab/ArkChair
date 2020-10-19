package fudan.se.lab2.controller.request;

/**
 * @author LBW
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String organization;
    private String region;
    private String fullname;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, String organization, String region, String fullname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.organization = organization;
        this.region = region;
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

