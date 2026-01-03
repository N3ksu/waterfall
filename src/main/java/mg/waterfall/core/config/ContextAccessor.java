package mg.waterfall.core.config;

import jakarta.servlet.ServletContext;
import mg.waterfall.core.web.routing.router.Router;

import java.util.Objects;
import java.util.Properties;

public final class ContextAccessor {
    private final ServletContext context;

    public ContextAccessor(ServletContext context) {
        Objects.requireNonNull(context, "Cannot access a null context");

        this.context = context;
    }

    public void setProperties(Properties properties) {
        context.setAttribute(ContextKey.PROPERTIES, properties);
    }

    public Properties getProperties() {
        return (Properties) context.getAttribute(ContextKey.PROPERTIES);
    }

    public void setRouter(Router router) {
        context.setAttribute(ContextKey.ROUTER, router);
    }

    public Router getRouter() {
        return (Router) context.getAttribute(ContextKey.ROUTER);
    }

    public void registerSelf() {
        context.setAttribute(ContextKey.ACCESSOR, this);
    }

    public static ContextAccessor getContextAccessor(ServletContext context) {
        return (ContextAccessor) context.getAttribute(ContextKey.ACCESSOR);
    }

    public static final class ContextKey {
        public static final String ACCESSOR = "wf.context.accessor";
        public static final String PROPERTIES = "wf.properties";
        public static final String ROUTER = "wf.router";
    }
}
