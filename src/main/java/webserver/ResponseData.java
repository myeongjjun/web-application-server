package webserver;

public class ResponseData {

    private String path;
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

    public void addCookie(String cookie) {
        if (this.cookie != null) {
            StringBuilder sb = new StringBuilder(this.cookie);
            sb.append(cookie);
            sb.append(";");
            this.cookie = sb.toString();
        } else {
            this.cookie = cookie;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
