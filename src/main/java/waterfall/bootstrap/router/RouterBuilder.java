package waterfall.bootstrap.router;

import waterfall.core.route.Route;
import waterfall.bootstrap.route.RouteBuilder;
import waterfall.core.router.Router;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class RouterBuilder {
    public static Router build(Map<String, Method> m) {
        Set<Route> routes = new HashSet<>();

        for (Entry<String, Method> e: m.entrySet()) {
            Route route = RouteBuilder.build(e.getKey(), e.getValue());
            routes.add(route);
        }

        return new Router(routes);
    }
}
