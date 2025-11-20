package waterfall.component.ui;

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

    public void setAttribute(String attr, Object value) {
        attributes.put(attr, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
