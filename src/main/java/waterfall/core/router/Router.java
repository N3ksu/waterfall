package waterfall.core.router;

import waterfall.core.route.Route;

import java.util.Set;

public class Router {
    private final Set<Route> routes;

    public Router(Set<Route> routes) {
        this.routes = routes;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public Route findRoute(String uri) {
        for (Route route: routes)
            if (route.getRgxPattern().matcher(uri).matches())
                return route;

        return null;
    }
}
