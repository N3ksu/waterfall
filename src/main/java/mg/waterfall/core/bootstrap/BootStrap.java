package mg.waterfall.core.bootstrap;

import jakarta.servlet.ServletContext;
import mg.waterfall.core.exception.technical.io.FileNotFoundException;
import mg.waterfall.core.exception.technical.io.FileLoadException;
import mg.waterfall.core.config.ContextAccessor;
import mg.waterfall.core.web.routing.http.method.HttpMethod;
import mg.waterfall.core.web.routing.route.Route;
import mg.waterfall.core.web.routing.router.Router;
import mg.waterfall.api.annotation.controller.Controller;
import mg.waterfall.api.annotation.request.mapping.RequestMapping;
import mg.waterfall.core.meta.util.IOReflectionUtil;
import mg.waterfall.core.util.tuple.Pair;

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

        Router router = setupRouter(methodAndRequestMappingSet);
        contextAccessor.setRouter(router);

        // Saving the contextAccessor into the context
        contextAccessor.registerSelf();
    }

    private static Router setupRouter(Set<Pair<Method, RequestMapping>> methodAndRequestMappingSet) {
        Set<Route.RouteBlueprint> routeBlueprints = new HashSet<>();

        for (Pair<Method, RequestMapping> methodAndRequestMapping : methodAndRequestMappingSet) {
            RequestMapping requestMapping = methodAndRequestMapping.right();
            for (HttpMethod httpMethod : requestMapping.method())
                routeBlueprints.add(new Route.RouteBlueprint(methodAndRequestMapping.left(), requestMapping.value(), httpMethod));
        }

        Router.Builder routerBuilder = new Router.Builder();
        return routerBuilder.build(routeBlueprints);
    }
}
