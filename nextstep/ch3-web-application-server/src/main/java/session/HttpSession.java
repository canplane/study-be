package session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
    private final String id;
    public HttpSession(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    private final Map<String, Object> store = new HashMap<>();
    public void setAttribute(String name, Object value) {
        store.put(name, value);
    }
    public Object getAttribute(String name) {
        return store.get(name);
    }
    public void removeAttribute(String name) {
        store.remove(name);
    }
    public void invalidate() {
        store.clear();
    }
}
