package waterfall.net;

import jakarta.servlet.ServletContext;
import waterfall.constant.WFC;

import java.lang.reflect.Method;
import java.util.Map;

public class Router {
    // TODO make this implements the singleton pattern
    public static Map<String, Method> getRoutes(ServletContext ctx) {
        return (Map<String, Method>) ctx.getAttribute(WFC.ROUTES_CTX_ATTR_NAME);
    }
}
