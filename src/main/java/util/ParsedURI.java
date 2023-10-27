package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParsedURI {
    public String path;
    public Map<String, String> params = null;

    public ParsedURI(String uri) throws IOException {
        String[] li = uri.split("\\?");
        path = li[0];
        if (li.length > 1) {
            params = parseParams(li[1]);
        }
    }

    public static Map<String, String> parseParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String _param : queryString.split("&")) {
            String[] p = _param.split("=");
            params.put(p[0], p[1]);
        }
        return params;
    }
}