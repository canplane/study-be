package controller;

import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.IOException;

import org.slf4j.*;

/** Servlet */
public interface Controller {
    public static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    void service(HttpRequest req, HttpResponse res) throws IOException;
}




