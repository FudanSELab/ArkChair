package fudan.se.lab2.domain;

public class TokenProcessor {
    private String token;
    private String userType;

    public TokenProcessor(){
        // no codes here
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public String getUserType() {
        return userType;
    }
}
