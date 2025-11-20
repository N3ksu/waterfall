package waterfall.bootstrap;

import jakarta.servlet.ServletContext;
import waterfall.annotation.Controller;
import waterfall.annotation.Url;
import waterfall.key.WaterFallKey;
import waterfall.bootstrap.net.router.RouterBuilder;
import waterfall.reflection.IOReflectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class WaterFallBootStrap {
    private final ServletContext ctx;
    private final RouterBuilder routerBuilder;

    public WaterFallBootStrap(ServletContext ctx) {
        this.ctx = ctx;
        routerBuilder = new RouterBuilder();
    }

    public void boot()
            throws IOException, URISyntaxException, ClassNotFoundException {
        bootConfig();
        bootRouter();
    }

    private void bootConfig() throws IOException {
        // TODO What to do if the client doesn't have the properties file
        InputStream in = ctx.getResourceAsStream(WaterFallKey.CONFIG_FILE_URI);
        Properties props = new Properties();
        props.load(in);

        for (Map.Entry<Object, Object> prop: props.entrySet())
            ctx.setAttribute(prop.getKey().toString(), prop.getValue()); // We didn't need to convert the Value into a string

        in.close();
    }

    private void bootRouter() throws IOException, URISyntaxException, ClassNotFoundException {
        ctx.setAttribute(WaterFallKey.ROUTER_CTX_ATTR_NAME, routerBuilder.build(retrieveRawRoutes()));
    }

    private Map<String, Method> retrieveRawRoutes()
            throws IOException, URISyntaxException, ClassNotFoundException {
        // TODO what if controllerPackage was null
        String controllerPackage = (String) ctx
                .getAttribute(WaterFallKey.CONTROLLER_PACKAGE_CONFIG_PARAM_NAME);

        Set<Method> methods = IOReflectionUtil
                .findAnnotatedMethodsInAnnotatedClasses(controllerPackage, Url.class, Controller.class);

        Map<String, Method> routes = new HashMap<>();

        for(Method method : methods) {
            Url url = method.getAnnotation(Url.class);
            routes.put(url.pattern(), method);
        }

        return routes;
    }
}
