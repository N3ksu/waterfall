package waterfall.core.router;

import waterfall.component.http.HttpMethod;
import waterfall.core.route.Route;

import java.util.Map;
import java.util.Set;

public class Router {
    private final Map<HttpMethod, Set<Route>> routes;

    public Router(Map<HttpMethod, Set<Route>> routes) {
        this.routes = routes;
    }

    public Map<HttpMethod, Set<Route>> getRoutes() {
        return routes;
    }

    public Route findRoute(HttpMethod httpMethod, String uri) {
        for (Route route: routes.get(httpMethod))
            if (route.getHttpMethod().equals(httpMethod) && route.getRgxPattern().matcher(uri).matches())
                return route;

        return null;
    }
}
