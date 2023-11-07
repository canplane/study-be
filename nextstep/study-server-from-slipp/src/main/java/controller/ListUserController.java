package controller;

import db.DataBase;
import model.*;

import java.io.IOException;
import java.util.Collection;

import static util.IOUtils.*;

public class ListUserController extends AbstractController {
    private byte[] makeHTML() throws IOException {
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

    private boolean isValid(HttpRequest req) {
        // 302 redirect일 때는 재요청이니까 브라우저가 다시 쿠키를 보내지는 않는가 봄.
        return Boolean.parseBoolean(req.getCookie("logined"));
    }

    @Override
    public void doGet(HttpRequest req, HttpResponse res) throws IOException {
        if (isValid(req)) {
            res.forwardContent(makeHTML());
        } else {
            res.redirect("/user/login.html");
        }
        res.redirect("/index.html");
    }
}
