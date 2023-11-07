package controller;

import model.*;

import java.io.IOException;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        HttpMethod method = req.getMethod();

        if (method.isPost()) {
            doPost(req, res);
        } else {
            doGet(req, res);
        }
    }

    protected void doPost(HttpRequest req, HttpResponse res) throws IOException {}
    protected void doGet(HttpRequest req, HttpResponse res) throws IOException {}
}
