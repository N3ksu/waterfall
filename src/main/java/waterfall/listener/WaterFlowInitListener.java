package waterfall.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import waterfall.annotation.Controller;
import waterfall.annotation.Url;
import waterfall.constant.WFConst;
import waterfall.util.ReflectionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

@WebListener
public class WaterFlowInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        loadConfig(ctx);
        loadRoutes(ctx);
    }

    private void loadConfig(ServletContext ctx) {
        try (InputStream in = ctx.getResourceAsStream(WFConst.WEBAPP_CONFIG_FILE_URI)){
            Properties props = new Properties();
            props.load(in);

            for (Entry<Object, Object> prop: props.entrySet())
                ctx.setAttribute(prop.getKey().toString(), prop.getValue());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadRoutes(ServletContext ctx) {
        ctx.setAttribute(WFConst.ROUTES_CTX_ATTR_NAME, getRoutes(ctx));
    }

    private Map<String, Method> getRoutes(ServletContext ctx) {
        try {
            // TODO what if controllerPackage was null
            String controllerPackage = (String) ctx.getAttribute(WFConst.WEBAPP_CONTROLLER_PACKAGE_CONFIG_PARAM_NAME);
            Set<Method> methods = ReflectionUtil
                    .findAnnotatedMethodsInAnnotatedClasses(controllerPackage, Url.class, Controller.class);

            Map<String, Method> routes = new HashMap<>();
            for(Method method : methods) {
                Url url = method.getAnnotation(Url.class);
                routes.put(url.url(), method);
            }

            return routes;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
