package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static util.IOUtils.readData;

public class HTTPMessage {
    public String[] startLine;
    public Map<String, String> header;
    public String body;

    public HTTPMessage(BufferedReader reader) throws IOException {
        startLine = readStartLine(reader);
        body = readBody(reader, header = readHeader(reader));
    }

    public static String[] readStartLine(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        s = URLDecoder.decode(s, "UTF-8");
        return s.split(" ");
    }
    public static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        Map<String, String> header = new HashMap<>();

        String s;
        while ((s = reader.readLine()) != null && !s.isEmpty()) {
            String[] p = s.split(": ");
            header.put(p[0], p[1]);
        }

        return header;
    }
    public static String readBody(BufferedReader reader, Map<String, String> header) throws IOException {
        String body = null;

        String s = header.get("Content-Length");
        if (s != null) {
            body = readData(reader, Integer.parseInt(s));

            String contentType = header.get("Content-Type");
            if ("application/x-www-form-urlencoded".equals(contentType)) {
                body = URLDecoder.decode(body, "UTF-8");
            }
        }

        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", startLine) + "\n");
        header.forEach((k, v) -> {
            sb.append(k + ": " + v + "\n");
        });
        if (body != null) {
            sb.append(body);
        }
        return sb.toString();
    }
}