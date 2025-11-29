package waterfall.bootstrap.router;

import waterfall.component.annotation.request.mapping.RequestMapping;
import waterfall.core.route.Route;
import waterfall.bootstrap.route.RouteBuilder;
import waterfall.core.router.Router;
import waterfall.util.tuple.Pair;

import java.lang.reflect.Method;
import java.util.Set;

public final class RouterBuilder {
    private final RouteBuilder routeBuilder;

    public RouterBuilder() {
        routeBuilder = new RouteBuilder();
    }

    public Router build(Set<Pair<Method, RequestMapping>> pairs) throws Exception {
        Router router = new Router();

        for (Pair<Method, RequestMapping> pair : pairs)
            for (Route route: routeBuilder.build(pair.getLeft(), pair.getRight()))
                router.add(route);

        return router;
    }
}
