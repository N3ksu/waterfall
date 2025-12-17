package waterfall.kernel.bootstrap;

import jakarta.servlet.ServletContext;
import waterfall.kernel.constant.Constant;
import waterfall.kernel.routing.router.Router;
import waterfall.api.annotation.controller.Controller;
import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.kernel.meta.util.IOReflectionUtil;
import waterfall.kernel.util.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

public final class BootStrap {
    private final ServletContext ctx;

    public BootStrap(ServletContext ctx) {
        this.ctx = ctx;
    }

    public void bootstrap() throws Exception {
        bootstrapConfig();
        bootstrapRouter();
    }

    private void bootstrapConfig() throws Exception {
        // TODO What to do if the client doesn't have the properties file
        Properties props = new Properties();
        props.load(ctx.getResourceAsStream(Constant.Config.CONFIG_FILE_URI));
        props.forEach((key, value) -> ctx.setAttribute(key.toString(), value));
    }

    private void bootstrapRouter() throws Exception {
        String controllerPackage = (String) ctx.getAttribute(Constant.Config.CONTROLLER_PACKAGE_CONFIG_PARAM_NAME);

        Set<Pair<Method, RequestMapping>> methodAndAnnotationPairs = IOReflectionUtil
                .findMethodAndAnnotationPairs(controllerPackage, Controller.class, RequestMapping.class);

        ctx.setAttribute(Constant.Context.ROUTER_CTX_ATTR_NAME, Router.Builder.build(methodAndAnnotationPairs));
    }
}
