package controller;

import db.DataBase;
import model.*;

import java.io.IOException;

public class LoginController extends AbstractController {
    private boolean isValid(HttpRequest req) {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");

        System.out.println(userId + " " + password);

        User user = DataBase.findUserById(userId);
        return user != null && (user.getPassword()).equals(password);
    }

    @Override
    public void doPost(HttpRequest req, HttpResponse res) throws IOException {
        if (isValid(req)) {
            res.setCookie("logined", "true");
            res.redirect("/index.html");
        } else {
            res.redirect("/user/login_failed.html");
        }
    }
}
