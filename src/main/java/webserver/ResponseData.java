package webserver;

public class ResponseData {

    private String host;
    private String cookie;

    public ResponseData() {
    }

    public String getCookie() {
        return cookie;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
