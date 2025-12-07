package waterfall.api.ui;

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

    public ModelView setView(String view) {
        this.view = view;
        return this;
    }

    public ModelView setAttribute(String attr, Object value) {
        attributes.put(attr, value);
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
