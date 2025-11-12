package waterfall.view;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String view;
    private final Map<String, Object> attributes;

    public ModelView() {
        attributes = new HashMap<>();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setAttribute(String attribute, Object value) {
        attributes.put(attribute, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
