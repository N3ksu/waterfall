package waterfall.kernel.routing.router;

import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.routing.route.Route;

import java.util.*;

public class Router {
    private final Map<HttpMethod, Set<Route>> routes;

    public Router() {
        routes = new HashMap<>();

        Arrays.stream(HttpMethod.values())
                .forEach(httpMethod -> routes.put(httpMethod, new HashSet<>()));
    }

    public void add(Route route) {
        routes.get(route.getHttpMethod()).add(route);
    }

    public Route findRoute(HttpMethod httpMethod, String uri) {
        for (Route route: routes.get(httpMethod))
            if (route.getRegexPattern().matcher(uri).matches()) return route;

        return null;
    }

    public static final class Builder {
        public static Router build(Set<Route.RouteBlueprint> routeBlueprints) {
            Router router = new Router();

            for (Route.RouteBlueprint routeBlueprint : routeBlueprints)
                router.add(Route.Builder.build(routeBlueprint));

            return router;
        }
    }
}
