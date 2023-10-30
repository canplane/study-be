package model;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static util.IOUtils.readData;

public class HttpResponse {
    private static final String NL = "\r\n";

    private DataOutputStream dos;
    private String version = "HTTP/1.1";
    private int statusCode;
    private String statusText;

    private Map<String, String> headers = new HashMap<>();

    public void setStatusCode(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public void setHeader(String k, String v) {
        headers.put(k, v);
    }

    public HttpResponse(OutputStream out) throws IOException {
        dos = new DataOutputStream(out);
    }

    public void forward(byte[] body) throws IOException {
        setStatusCode(200, "OK");
        write(body);
    }
    public void redirect(String uri) throws IOException {
        setStatusCode(302, "FOUND");
        setHeader("Location", uri);
        write(null);
    }

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
        dos.writeBytes(NL);
    }
    private void writeBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
    }
}