package mg.waterfall.core.web.routing.http.media;

import mg.waterfall.api.annotation.json.Json;

import java.lang.reflect.Method;

public enum MediaType {
    VIEW,
    APPLICATION_JSON;

    public static MediaType mediaTypeOf(Method m) {
        if (m.isAnnotationPresent(Json.class))
            return APPLICATION_JSON;
        return VIEW;
    }
}
