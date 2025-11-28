package waterfall.bootstrap;

import jakarta.servlet.ServletContext;
import waterfall.bootstrap.router.RouterBuilder;
import waterfall.component.annotation.Controller;
import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.core.constant.WaterFallConstant;
import waterfall.core.reflection.IOReflectionUtil;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class WaterFallBootStrap {
    private final ServletContext ctx;

    public WaterFallBootStrap(ServletContext ctx) {
        this.ctx = ctx;
    }

    public void boot()
            throws Exception {
        bootConfig();
        bootRouter();
    }

    private void bootConfig() throws Exception {
        // TODO What to do if the client doesn't have the properties file
        InputStream in = ctx.getResourceAsStream(WaterFallConstant.CONFIG_FILE_URI);
        Properties props = new Properties();
        props.load(in);

        for (Map.Entry<Object, Object> prop: props.entrySet())
            ctx.setAttribute(prop.getKey().toString(), prop.getValue()); // We didn't need to convert the Value into a string

        in.close();
    }

    private void bootRouter() throws Exception {
        String controllerPackage = (String) ctx.getAttribute(WaterFallConstant.CONTROLLER_PACKAGE_CONFIG_PARAM_NAME);
        Set<SimpleEntry<Method, RequestMapping>> annotatedMethodEntries = IOReflectionUtil.findAnnotatedMethodEntriesInPackage(controllerPackage, RequestMapping.class, Controller.class);
        ctx.setAttribute(WaterFallConstant.ROUTER_CTX_ATTR_NAME, RouterBuilder.build(annotatedMethodEntries));
    }
}
