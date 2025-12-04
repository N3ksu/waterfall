package waterfall.kernel.bootstrap;

import jakarta.servlet.ServletContext;
import waterfall.kernel.routing.router.RouterBuilder;
import waterfall.api.annotation.Controller;
import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.constant.WFConstant;
import waterfall.kernel.reflection.IOReflectionUtil;
import waterfall.kernel.util.tuple.Pair;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class WFBootStrap {
    private final ServletContext ctx;
    private final RouterBuilder routerBuilder;

    public WFBootStrap(ServletContext ctx) {
        this.ctx = ctx;
        routerBuilder = new RouterBuilder();
    }

    public void boot() throws Exception {
        bootConfig();
        bootRouter();
    }

    private void bootConfig() throws Exception {
        // TODO What to do if the client doesn't have the properties file
        InputStream in = ctx.getResourceAsStream(WFConstant.CONFIG_FILE_URI);
        Properties props = new Properties();
        props.load(in);

        for (Map.Entry<Object, Object> prop: props.entrySet())
            ctx.setAttribute(prop.getKey().toString(), prop.getValue().toString());

        in.close();
    }

    private void bootRouter() throws Exception {
        String controllerPackage = (String) ctx.getAttribute(WFConstant.CONTROLLER_PACKAGE_CONF_PARAM_NAME);

        Set<Pair<Method, RequestMapping>> methodAndAnnotationPairs = IOReflectionUtil
                .findMethodAndAnnotationPairsInAnnotatedClassesInPackage(controllerPackage, Controller.class, RequestMapping.class);

        ctx.setAttribute(WFConstant.ROUTER_CTX_ATTR_NAME, routerBuilder.build(methodAndAnnotationPairs));
    }
}
