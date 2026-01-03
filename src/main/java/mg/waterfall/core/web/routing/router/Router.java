package mg.waterfall.core.web.routing.router;

import mg.waterfall.core.web.routing.http.method.HttpMethod;
import mg.waterfall.core.web.routing.route.Route;

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
            if (route.getRegexPattern().matcher(uri).matches())
                return route;

        return null;
    }

    public static final class Builder {
        private final Route.Builder routeBuilder;

        public Builder() {
            routeBuilder = new Route.Builder();
        }

        public Router build(Set<Route.RouteBlueprint> routeBlueprints) {
            Router router = new Router();

            for (Route.RouteBlueprint routeBlueprint : routeBlueprints)
                router.add(routeBuilder.build(routeBlueprint));

            return router;
        }
    }
}
