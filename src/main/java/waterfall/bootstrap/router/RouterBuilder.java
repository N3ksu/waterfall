package waterfall.bootstrap.router;

import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.core.route.Route;
import waterfall.bootstrap.route.RouteBuilder;
import waterfall.core.router.Router;

import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RouterBuilder {
    public static Router build(Set<SimpleEntry<Method, RequestMapping>> entries) throws Exception {
        Set<Route> routes = new HashSet<>();

        for (SimpleEntry<Method, RequestMapping> entry : entries) {
            List<Route> routeList = RouteBuilder.build(entry.getKey(), entry.getValue());
            routes.addAll(routeList);
        }

        return new Router(routes);
    }
}
