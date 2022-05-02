package webserver;

import util.HttpRequestUtils;

import java.util.Map;

import static webserver.HttpMethodType.GET;
import static webserver.HttpMethodType.POST;

public class RequestData {

    HttpMethodType methodType;
    String url;
    String host;
    String path;
    String cookie;
    Map<String, String> params;
    int contentLength;
    String body;

    public RequestData(HttpMethodType methodType, String url, String host, String path, Map<String, String> params, int contentLength, String body) {
        this.methodType = methodType;
        this.url = url;
        this.host = host;
        this.path = path;
        this.params = params;
        this.contentLength = contentLength;
        this.body = body;
    }

    public static RequestData requestGet(String url, String host) {
        String path = url;
        String params = "";
        if (url.contains("?")) {
            int index = url.indexOf("?");
            path = url.substring(0, index);
            params = url.substring(index + 1);
        }

        return new RequestData(GET, url, host, path, HttpRequestUtils.parseQueryString(params), 0, "");
    }

    public static RequestData requestPost(String url, String host, int contentLength, String body) {
        return new RequestData(POST, url, host, url, HttpRequestUtils.parseQueryString(body), contentLength, body);
    }

    public String addCookie(Map<String, String> headers) {
        return headers.getOrDefault("Cookie", "");
    }

    public String getCookie() {
        return cookie;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public HttpMethodType getMethodType() {
        return methodType;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
