package model;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import static util.HttpRequestUtils.*;
import static util.IOUtils.readData;


public class HttpRequest {
    private HttpMethod method;
    private String path;
    private Map<String, String> parameters = new HashMap<>();
    private String version;

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();

    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public String getParameter(String k) { return parameters.get(k); }

    public String getHeader(String k) { return headers.get(k); }
    public String getCookie(String k) { return cookies.get(k); }

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

        method = HttpMethod.valueOf(tokens[0]);

        String[] p = tokens[1].split("\\?");
        path = p[0];
        if (p.length > 1) {
            parameters = parseQueryString(p[1]);
        }

        version = tokens[2];
    }
    private void readHeaders(BufferedReader reader) throws IOException {
        String s;
        while ((s = reader.readLine()) != null && !s.isEmpty()) {
            Pair p = parseHeader(s);
            String k = p.getKey(), v = p.getValue();
            headers.put(k, v);
            if (k.equals("Cookie")) {
                cookies = parseCookies(v);
            }
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
                parameters = parseQueryString(_body);
            }
        }
    }
}