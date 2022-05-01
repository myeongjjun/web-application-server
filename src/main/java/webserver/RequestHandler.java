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
            response(in, out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(InputStream in, OutputStream out) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String header = br.readLine();

        log.debug(header);
         // 공백

        HttpMethodType methodType = getHttpMethodType(header);
        String url = getUrl(header);
        String host = parseHost(br);
        log.info("path => {}", url);
        log.info("host => {}", host);

        switch (methodType) {
            case GET:
                RequestData requestGet = RequestData.requestGet(url, host);
                requestMapping(requestGet, out);
                break;
            case POST:
                int contentLength = parseContentLength(br);
                String body = parsePostBody(br, contentLength);
                RequestData requestPost = RequestData.requestPost(url, host, contentLength, body);
                requestMapping(requestPost, out);
                break;
            default:
//                responseBody = "Hello World".getBytes();
                break;
        }


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

    private String parseHost(BufferedReader br) throws IOException {
        String line = br.readLine();
        while (!"".equals(line)) {
            log.debug(line);
            int index = line.indexOf(":");
            if (line.substring(0, index).equals("Host")) {
                return line.substring(index + 2);
            }
            line = br.readLine();
        }
        return "";
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

    private void requestMapping(RequestData req, OutputStream out) throws IOException {
        String path = req.getPath();
        Map<String, String> paramsMap = req.getParams();
        String host = req.getHost();
        DataOutputStream dos = new DataOutputStream(out);
        if (path.equals("/user/create")) {
            createUser(paramsMap);
            byte[] body = getBody(path);
            response302Header(dos, host, "/index.html");
            responseBody(dos, body);
        } else if (path.equals("/user/login")) {
            byte[] body = getBody(path);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } else {
            byte[] body = getBody(path);
            response200Header(dos, body.length);
            responseBody(dos, body);
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

    private void response302Header(DataOutputStream dos, String host, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://" + host + path +"\r\n");
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
