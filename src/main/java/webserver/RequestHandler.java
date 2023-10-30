package webserver;

import java.io.*;
import java.net.Socket;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.HttpRequest;
import model.HttpResponse;

import static util.HttpRequestUtils.parseCookies;



public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }


    private byte[] readResource(String path) throws IOException {
        return Files.readAllBytes(Paths.get("./webapp" + path));
    }

    private void userCreate(HttpRequest req) {
        User user = new User(
                req.getParameter("userId"),
                req.getParameter("password"),
                req.getParameter("name"),
                req.getParameter("email")
        );
        DataBase.addUser(user);

        log.debug("New user: {}", user);
    }
    private boolean userLogin(HttpRequest req) {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");

        User user = DataBase.findUserById(userId);
        return user != null && (user.getPassword()).equals(password);
    }
    private boolean userList(HttpRequest req) {
        // 302 redirect일 때는 재요청이니까 브라우저가 다시 쿠키를 보내지는 않는가 봄.
        String cookieString = req.getHeader("Cookie");
        if (cookieString == null) {
            return false;
        }
        Map<String, String> cookies = parseCookies(cookieString);

        return Boolean.parseBoolean(cookies.get("logined"));
    }
    private byte[] makeListHTML() throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(new String(readResource("/user/list1")));

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

        sb.append(new String(readResource("/user/list2")));

        String s = sb.toString();
        return s.getBytes("UTF-8");
    }

    public void run() {
        //log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest req = new HttpRequest(in);
            HttpResponse res = new HttpResponse(out);

            String path = req.getPath();

            if (path.endsWith(".html")) {
                res.setHeader("Content-Type", "text/html;charset=utf-8");
            } else if (path.endsWith(".css")) {
                res.setHeader("Content-Type", "text/css,*/*,q=0.1");
            }

            if (path.equals("/")) {
                res.redirect("/index.html");
            } else if (path.equals("/user/create")) {
                userCreate(req);
                res.redirect("/index.html");
            } else if (path.equals("/user/login")) {
                if (userLogin(req)) {
                    res.setHeader("Set-Cookie", "logined=true");
                    res.redirect("/index.html");
                } else {
                    res.redirect("/user/login_failed.html");
                }
            } else if (path.equals(("/user/list"))) {
                if (userList(req)) {
                    res.forward(makeListHTML());
                } else {
                    res.redirect("/user/login.html");
                }
            } else {
                res.forward(readResource(path));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
