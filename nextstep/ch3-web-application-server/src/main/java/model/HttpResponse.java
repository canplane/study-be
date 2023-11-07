package model;

import java.io.*;
import java.util.*;

import static util.IOUtils.readResource;


public class HttpResponse {
    private String version = "HTTP/1.1";
    private int statusCode;
    private String statusText;

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();

    public void setStatusCode(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public void setHeader(String k, String v) {
        headers.put(k, v);
    }
    public void setCookie(String k, String v) { cookies.put(k, v); }

    private DataOutputStream dos;
    public HttpResponse(OutputStream out) throws IOException {
        dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        if (path.endsWith(".js")) {
            setHeader("Content-Type", "application/javascript");
        } else if (path.endsWith(".css")) {
            setHeader("Content-Type", "text/css");
        } else {
            setHeader("Content-Type", "text/html;charset=utf-8");
        }

        setStatusCode(200, "OK");
        write(readResource(path));
    }
    public void forwardContent(byte[] content) throws IOException {
        setHeader("Content-Type", "text/html;charset=utf-8");

        setStatusCode(200, "OK");
        write(content);
    }

    public void redirect(String uri) throws IOException {
        setStatusCode(302, "FOUND");
        setHeader("Location", uri);
        write(null);
    }

    private static final String NL = "\r\n";
    private void write(byte[] body) throws IOException {
        if (body != null) {
            setHeader("Content-Length", String.valueOf(body.length));
        }

        writeResponseLine();
        writeHeaders();
        if (body != null) {
            writeBody(body);
        }
        dos.flush();
    }
    private void writeResponseLine() throws IOException {
        dos.writeBytes(String.format("%s %d %s", version, statusCode, statusText) + NL);
    }
    private void writeHeaders() throws IOException {
        for (String k : headers.keySet()) {
            dos.writeBytes(String.format("%s: %s", k, headers.get(k)) + NL);
        }
        if (!cookies.isEmpty()) {
            dos.writeBytes("Set-Cookie: ");
            for (String k : cookies.keySet()) {
                dos.writeBytes(String.format("%s=%s", k, cookies.get(k)) + ';');
            }
        }
        dos.writeBytes(NL);
    }
    private void writeBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
    }
}