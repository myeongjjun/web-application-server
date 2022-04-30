package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import static webserver.HttpMethodType.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            byte[] body = getResponse(in);

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getResponse(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String header = br.readLine();
        br.readLine(); // 공백
        String body = br.readLine();

        HttpMethodType methodType = getHttpMethodType(header);
        String url = getUrl(header);
        log.info("url => {}", url);

        switch (methodType) {
            case GET:
                requestGet(url);
                return getBody(url);
            case POST:
//                requestPost(url);
                break;
        }
        return "Hello World".getBytes();
    }

    private void requestGet(String url) {
        if (url.contains("?")) {
            int index = url.indexOf("?");
            String requestPath = url.substring(0, index);
            String params = url.substring(index + 1);
            Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(params);

            requestMapping(requestPath, paramsMap);
        }
    }

    private HttpMethodType getHttpMethodType(String header) {
        Pattern URL_PATH = Pattern.compile("(GET|POST|DELETE|PUT) .+ HTTP/1.1");
        Matcher matcher = URL_PATH.matcher(header);
        if (matcher.find()) {
            String s = matcher.group(1);
            return HttpMethodType.valueOf(s);
        }

        return null;
    }

    private void requestMapping(String requestPath, Map<String, String> paramsMap) {
        if (requestPath.equals("/user/create")) {
            createUser(paramsMap);
        }
    }

    private void createUser(Map<String, String> paramsMap) {
        String userId = paramsMap.get("userId");
        String password = paramsMap.get("password");
        String name = paramsMap.get("name");
        String email = paramsMap.get("email");
        User user = new User(userId, password, name, email);
        DataBase.addUser(user);
    }

    private byte[] getBody(String url) throws IOException {
        byte[] body = "".getBytes();
        File f = new File("webapp/" + url);
        if (f.isFile()) {
            log.info(f.getName());
            body = Files.readAllBytes(f.toPath());
        }
        return body;
    }

    private String getUrl(String header) {
        String path = "";
        path = extractPath(header);
        return path;
    }

    private String extractPath(String line) {
        Pattern URL_PATH = Pattern.compile("(?:GET|POST|DELETE) (.+) HTTP/1.1");
        Matcher matcher = URL_PATH.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
