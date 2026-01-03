package waterfall.kernel.bootstrap;

import jakarta.servlet.ServletContext;
import waterfall.kernel.exception.technical.io.FileNotFoundException;
import waterfall.kernel.exception.technical.io.FileLoadException;
import waterfall.kernel.config.ContextAccessor;
import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.routing.router.Router;
import waterfall.api.annotation.controller.Controller;
import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.kernel.meta.util.IOReflectionUtil;
import waterfall.kernel.util.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class BootStrap {
    public static final String CONFIG_FILE_URI = "/WEB-INF/waterfall.properties";
    public static final String CONTROLLER_PACKAGE_CONFIG_PARAM_KEY = "controller.package";

    public void boot(ServletContext context) {
        ContextAccessor contextAccessor = new ContextAccessor(context);

        // Client config file loading and saving into the context
        String configFile = CONFIG_FILE_URI;
        Properties properties = new Properties();

        try (InputStream in = context.getResourceAsStream(configFile)) {
            if (in == null) 
                throw new FileNotFoundException(configFile);
            
            properties.load(in);
            contextAccessor.setProperties(properties);
        } catch (IOException e) {
            throw new FileLoadException(configFile, e);
        }

        // Creating a router for the framework's request dispatcher
        String clientControllerPackage = properties.getProperty(CONTROLLER_PACKAGE_CONFIG_PARAM_KEY);

        Set<Pair<Method, RequestMapping>> methodAndRequestMappingSet =
                IOReflectionUtil.findAnnotatedMethods(clientControllerPackage, Controller.class, RequestMapping.class);

        Set<Route.RouteBlueprint> routeBlueprints = new HashSet<>();

        for (Pair<Method, RequestMapping> methodAndRequestMapping : methodAndRequestMappingSet) {
            RequestMapping requestMapping = methodAndRequestMapping.right();
            for (HttpMethod httpMethod : requestMapping.method())
                routeBlueprints.add(new Route.RouteBlueprint(methodAndRequestMapping.left(), requestMapping.value(), httpMethod));
        }

        Router router = Router.Builder.build(routeBlueprints);
        contextAccessor.setRouter(router);

        // Saving the contextAccessor into the context
        contextAccessor.registerSelf();
    }
}
