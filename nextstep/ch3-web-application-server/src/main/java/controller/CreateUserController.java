package controller;

import db.DataBase;
import model.*;

import java.io.IOException;

public class CreateUserController extends AbstractController {
    private void makeUser(HttpRequest req) {
        User user = new User(
                req.getParameter("userId"),
                req.getParameter("password"),
                req.getParameter("name"),
                req.getParameter("email")
        );
        DataBase.addUser(user);
    }

    @Override
    public void doPost(HttpRequest req, HttpResponse res) throws IOException {
        makeUser(req);
        res.sendRedirect("/index.html");
    }
}
