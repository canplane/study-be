package controller;

import db.DataBase;
import model.*;

import java.io.IOException;

public class UserLoginController implements Controller {
    private boolean isValid(HttpRequest req) {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");

        User user = DataBase.findUserById(userId);
        return user != null && (user.getPassword()).equals(password);
    }

    public void service(HttpRequest req, HttpResponse res) throws IOException {
        if (isValid(req)) {
            res.setHeader("Set-Cookie", "logined=true");
            res.sendRedirect("/index.html");
        } else {
            res.sendRedirect("/user/login_failed.html");
        }
    }
}
