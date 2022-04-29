package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            String filePath = getFilePath(in);
            log.info("filePath => {}", filePath);

            byte[] body = "Hello World".getBytes();
            body = getBody(filePath, body);

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getBody(String filePath, byte[] body) throws IOException {
        File f = new File("webapp/" + filePath);
        if (f.isFile()) {
            log.info(f.getName());
            body = Files.readAllBytes(f.toPath());
        }
        return body;
    }

    private String getFilePath(InputStream in) throws IOException {
        String filePath = "";
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        while (!"".equals(line) && filePath.equals("")) {
            if (line == null) {
                return "";
            }
            filePath = extractPath(line);
            line = br.readLine();
        }
        return filePath;
    }

    private String extractPath(String line) {
        Pattern URL_PATH = Pattern.compile("GET ([\\w\\.\\/]+) HTTP/1.1");
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
