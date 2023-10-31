package controller;

import model.*;

import java.io.IOException;

public interface Controller {
    void service(HttpRequest req, HttpResponse res) throws IOException;
}




