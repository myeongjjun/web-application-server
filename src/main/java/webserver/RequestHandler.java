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
import util.IOUtils;

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
//            printRequest(in);
            byte[] body = getResponse(in);

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void printRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        while (line != null || !line.equals("")) {
            log.debug(line);
            line = br.readLine();
        }
    }

    private byte[] getResponse(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String header = br.readLine();

        log.debug(header);
         // 공백

        HttpMethodType methodType = getHttpMethodType(header);
        String url = getUrl(header);
        log.info("url => {}", url);

        switch (methodType) {
            case GET:
                requestGet(url);
                return getBody(url);
            case POST:
                int contentLength = parseContentLength(br);
                String body = parsePostBody(br, contentLength);
                requestPost(url, body);
                return getBody(url);
        }
        return "Hello World".getBytes();
    }

    private String parsePostBody(BufferedReader br, int contentLength) throws IOException {
        // POST body 까지 readLine
        String body = br.readLine();
        while (!"".equals(body)) {
            log.debug(body);
            body = br.readLine();
        }
        // POST body 까지 readLine
        return IOUtils.readData(br, contentLength);
    }


    private int parseContentLength(BufferedReader br) throws IOException {
        String line = br.readLine();
        while (!"".equals(line)) {
            log.debug(line);
            int index = line.indexOf(":");
            if (line.substring(0, index).equals("Content-Length")) {
                return Integer.parseInt(line.substring(index + 2));
            }
            line = br.readLine();
        }
        return 0;
    }

    private void requestPost(String url, String body) {
        Map<String, String> paramsMap = HttpRequestUtils.parseQueryString(body);
        // POST 요청은 url이 requestPath
        requestMapping(url, paramsMap);
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
        if (header == null) {
            return NONE;
        }
        Pattern URL_PATH = Pattern.compile("(GET|POST|DELETE|PUT) .+ HTTP/1.1");
        Matcher matcher = URL_PATH.matcher(header);
        if (matcher.find()) {
            String s = matcher.group(1);
            return HttpMethodType.valueOf(s);
        }

        return NONE;
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
        if (line == null) {
            return "";
        }
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
