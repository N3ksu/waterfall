package waterfall.kernel.routing.router;

import waterfall.api.annotation.request.mapping.RequestMapping;
import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.util.tuple.Pair;

import java.lang.reflect.Method;
import java.util.*;

public class Router {
    private final Map<HttpMethod, Set<Route>> routes;

    public Router() {
        routes = new HashMap<>();

        Arrays.stream(HttpMethod.values())
                .forEach(httpMethod -> routes.put(httpMethod, new HashSet<>()));
    }

    public void add(final Route route) {
        routes.get(route.getHttpMethod()).add(route);
    }

    public Route findRoute(final HttpMethod httpMethod, final String uri) {
        for (Route route: routes.get(httpMethod))
            if (route.getRgxPattern().matcher(uri).matches()) return route;

        return null;
    }

    public static final class Builder {
        public static Router build(final Set<Pair<Method, RequestMapping>> pairs) throws Exception {
            final Router router = new Router();

            for (Pair<Method, RequestMapping> pair : pairs)
                for (Route route: Route.Builder.build(pair.getLeft(), pair.getRight()))
                    router.add(route);

            return router;
        }
    }
}
