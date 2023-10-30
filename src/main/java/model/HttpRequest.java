package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static util.IOUtils.readData;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> parameters = new HashMap<>();

    private String version;
    private Map<String, String> headers = new HashMap<>();

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getParameter(String k) { return parameters.get(k); }

    public String getHeader(String k) { return headers.get(k); }

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        readRequestLine(reader);
        readHeaders(reader);
        readBody(reader);
    }

    private void readRequestLine(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        s = URLDecoder.decode(s, "UTF-8");
        String[] tokens = s.split(" ");

        method = tokens[0];
        parseUri(tokens[1]);
        version = tokens[2];
    }
    private void readHeaders(BufferedReader reader) throws IOException {
        String s;
        while ((s = reader.readLine()) != null && !s.isEmpty()) {
            String[] p = s.split(": ");
            headers.put(p[0], p[1]);
        }
    }
    private void readBody(BufferedReader reader) throws IOException {
        String s;

        s = getHeader("Content-Length");
        if (s != null) {
            int sz = Integer.parseInt(s);
            String _body = readData(reader, sz);

            s = getHeader("Content-Type");
            if ("application/x-www-form-urlencoded".equals(s)) {
                _body = URLDecoder.decode(_body, "UTF-8");
                parseParams(_body);
            }
        }
    }

    private void parseUri(String uri) {
        String[] tokens = uri.split("\\?");
        path = tokens[0];
        if (tokens.length > 1) {
            parseParams(tokens[1]);
        }
    }
    private void parseParams(String queryString) {
        for (String _param : queryString.split("&")) {
            String[] p = _param.split("=");
            parameters.put(p[0], p[1]);
        }
    }
}