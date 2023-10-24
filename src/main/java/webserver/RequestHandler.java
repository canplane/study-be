package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    private String[] getRequestHeader(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> _lines = new ArrayList<>();
        String line;
        while (!"".equals(line = reader.readLine()) && line != null) {
            _lines.add(line);
        }
        String[] lines = _lines.toArray(new String[0]);
        return lines;
    }
    private String getUrl(String[] requestHeader) {
        String requestHeaderFirstLine = requestHeader[0];
        String url = requestHeaderFirstLine.split(" ")[1];
        return url;
    }
    private byte[] getFileByUrl(String url) throws IOException {
        if (url.equals("/")) {
            url = "/index.html";
        }
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        return body;
    }
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            /** 요구사항 1 */
            String[] requestHeader = getRequestHeader(in);
            String url = getUrl(requestHeader);
            byte[] body = getFileByUrl(url);


            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            //byte[] body = "Hello World, 상훈!".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
