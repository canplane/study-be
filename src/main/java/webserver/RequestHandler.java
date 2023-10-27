package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder; // application/x-www-form-urlencoded (MDN 참고)
/* form 태그를 이용한 데이터 전송 시,
모두 utf-8 기반의 urlencoded (ascii 아닌 걸 모두 %..로 바꾸는 것)를 이용함 (POST의 경우에도 body가 Content-Type=x-www-form-urlencoded가 default)
근데 POST의 경우에는 html의 charset으로 문자를 읽은 걸 urlencoded 돌려버림.
즉, GET 방식은 URI 표준에 따라 UTF-8이 강제지만, POST 방식은 meta charset에 따름. -> 근데 어떤 charset이든 GET이나 POST나 기본은 그걸 urlencode해서 전송.

아무튼 html/서버 모두 utf-8로 맞추고, form이나 url 해석 시에는 utf-8이 urlencoded된 후 쿼리 스트링 형태로 되어 있는 게 기준이 되는 게 편함.
form이나 url을 받을 때는 urldecode를 먼저 하고 받기! (POST form의 경우에도 Content-Type을 따로 지정한 적이 없댜면 urlencoded되어서 온다.)
일반 파일이나 html,js,css 등 리소스 전송될 때는 urlencoded되는 건 아님.

+ 만약 js, css가 인코딩이 html meta와 다르다면? -> CSS는 @charset="UTF-8"으로 css 파일 내에 선언, JS는 <script charset="UTF-8" ...>로 사용

Q. 서버 소켓 getBytes 시 utf-8인지 어떻게 알고 그에 맞춰 스트링으로 변환하는 건지?
-> JVM의 Charset.defaultCharset()이 기반 시스템의 charset이라서. 그리고 내 기반 시스템의 charset이 utf-8이다. (우분투 wsl이 내 환경에선 $LANG=C.UTF-8로 되어 있다.)
-> inputstreamreader와 같은 게 인코딩 설정 안하면 디폴트 charset 따라가는것
 */
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.IOUtils.readData;
import static util.HttpRequestUtils.parseCookies;

class URL {
    public String host;
    public Map<String, String> params = null;

    URL(String urlString) {
        String[] li = urlString.split("\\?");
        host = li[0];
        if (host.equals("/")) {
            host = "/index.html";
        }
        System.out.println(host);

        if (li.length > 1) {
            params = parseParams(li[1]);
        }
    }

    public static Map<String, String> parseParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String _param : queryString.split("&")) {
            String[] p = _param.split("=");
            params.put(p[0], p[1]);
        }
        return params;
    }
}

class HTTPRequestMessage {
    public String[] requestLine;
    public Map<String, String> fields;
    public String body;
}


public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    private HTTPRequestMessage parseRequestMessage(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        HTTPRequestMessage requestMessage = new HTTPRequestMessage();

        String s;

        s = reader.readLine();
        requestMessage.requestLine = s.split(" ");

        requestMessage.fields = new HashMap<>();
        while ((s = reader.readLine()) != null && !s.isEmpty()) {
            String[] p = s.split(": ");
            (requestMessage.fields).put(p[0], p[1]);

            System.out.println("- " + s);
        }

        requestMessage.body = null;
        if ((s = (requestMessage.fields).get("Content-Length")) != null) {
            requestMessage.body = URLDecoder.decode(readData(reader, Integer.parseInt(s)), "UTF-8");
        }

        return requestMessage;
    }

    private byte[] getFileByUrl(String host) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + host));
        //System.out.println(new File("./webapp" + url).toPath());
        return body;
    }

    // GET /user/create
    private void userCreate(Map<String, String> params) {

        User user = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );
        DataBase.addUser(user);
        System.out.println(user);
    }
    private boolean userLogin(Map<String, String> params) {
        User user = DataBase.findUserById(params.get("userId"));
        return user != null && (params.get("password")).equals(user.getPassword());
    }
    private boolean userList(Map<String, String> params) {
        String cookieString = params.get("Cookie");
        if (cookieString == null) {
            return false;
        }
        Map<String, String> cookies = parseCookies(cookieString);

        return Boolean.parseBoolean(cookies.get("logined"));
    }

    private byte[] makeListHTML() throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(new String(getFileByUrl("/user/list1")));

        sb.append("<div class=\"container\" id=\"main\">\r\n");
        sb.append("   <div class=\"col-md-10 col-md-offset-1\">\r\n");
        sb.append("      <div class=\"panel panel-default\">\r\n");
        sb.append("          <table class=\"table table-hover\">\r\n");
        sb.append("              <thead><tr><th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th></tr></thead>\r\n");
        sb.append("              <tbody>\r\n");

        Collection<User> users = DataBase.findAll();
        int i = 0;
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<th score=\"row\">" + ++i + "</th>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            sb.append("</tr>\r\n");
        }
        sb.append("              </tbody>\r\n");
        sb.append("          </table>\r\n");
        sb.append("        </div>\r\n");
        sb.append("    </div>\r\n");
        sb.append("</div>\r\n");

        sb.append(new String(getFileByUrl("/user/list2")));

        String s = sb.toString();
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HTTPRequestMessage req = parseRequestMessage(in);
            URL url = new URL(req.requestLine[1]);

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            //byte[] body = "Hello World".getBytes();

            if ((url.host).equals("/user/create")) {
                userCreate(URL.parseParams(req.body));
                response302Header(dos, "/index.html");
            } else if ((url.host).equals("/user/login")) {
                boolean valid = userLogin(URL.parseParams(req.body));
                if (valid) {
                    response302LoginHeader(dos, "/index.html");
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }
            } else if ((url.host).equals(("/user/list.html"))) {
                boolean valid = userList(req.fields);
                if (valid) {
                    byte[] body = makeListHTML();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    response302Header(dos, "/user/login.html");
                }
            } else if ((url.host).endsWith(".css")) {
                byte[] body = getFileByUrl(url.host);
                response200CSSHeader(dos, body.length);
                responseBody(dos, body);
            } else {
                byte[] body = getFileByUrl(url.host);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
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
    private void response200CSSHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css,*/*,q=0.1\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void response302LoginHeader(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
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
