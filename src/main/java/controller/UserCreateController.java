package controller;

import db.DataBase;
import model.*;

import java.io.IOException;

public class UserCreateController implements Controller {
    private void makeUser(HttpRequest req) {
        User user = new User(
                req.getParameter("userId"),
                req.getParameter("password"),
                req.getParameter("name"),
                req.getParameter("email")
        );
        DataBase.addUser(user);
    }

    public void service(HttpRequest req, HttpResponse res) throws IOException {
        makeUser(req);
        res.sendRedirect("/index.html");
    }
}
