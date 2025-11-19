package waterfall.net;

import jakarta.servlet.ServletContext;
import waterfall.annotation.Controller;
import waterfall.annotation.Url;
import waterfall.constant.WFC;
import waterfall.util.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO Maybe all of these shouldn't be static
public class Router {
    public static Map<String, Method> getRoutes(ServletContext ctx) {
        return (Map<String, Method>) ctx.getAttribute(WFC.WEBAPP_ROUTES_CTX_ATTR_NAME);
    }
}
