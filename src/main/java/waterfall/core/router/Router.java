package waterfall.core.router;

import waterfall.core.route.Route;

import java.util.List;

public class Router {
    private final List<Route> routes;

    public Router(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Route findRoute(String uri) {
        for (Route route: routes)
            if (route.getRgxPattern().matcher(uri).matches())
                return route;

        return null;
    }
}
