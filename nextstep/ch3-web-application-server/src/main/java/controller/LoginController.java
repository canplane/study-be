package controller;

import db.DataBase;
import model.*;
import session.HttpSession;
import session.HttpSessions;

import java.io.IOException;

public class LoginController extends AbstractController {
    private User getUser(HttpRequest req) {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        log.debug("{} {}", userId, password);

        User user = DataBase.findUserById(userId);
        if (user != null && (user.getPassword()).equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public void doPost(HttpRequest req, HttpResponse res) throws IOException {
        User user = getUser(req);
        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            //res.setCookie("logined", "true");
            res.sendRedirect("/index.html");
        } else {
            res.sendRedirect("/user/login_failed.html");
        }
    }
}
