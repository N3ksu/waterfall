package waterfall.kernel.request.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.meta.binding.RouteArgumentResolver;
import waterfall.kernel.routing.http.media.MediaType;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.routing.router.Router;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.api.ui.ModelView;
import waterfall.kernel.response.endpoint.EndPoint;

public final class Dispatcher {
    private final Router router;
    private final RouteArgumentResolver routeArgumentResolver;

    public Dispatcher(final Router router) {
        this.router = router;
        routeArgumentResolver = new RouteArgumentResolver();
    }

    public void forward(final HttpServletRequest req, final HttpServletResponse res) throws Exception {
        Route route;
        if ((route = findRoute(req)) == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, req.getServletPath());
            return;
        }

        final Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(route.getAction().getDeclaringClass());
        final Object[] args = routeArgumentResolver.resolve(route, req);
        final Class<?> returnType = route.getAction().getReturnType();

        if (ModelView.class.equals(returnType)) {
            EndPoint.MV.forward(req, res, route, controller, args);
        } else if (route.produces(MediaType.APPLICATION_JSON)) {
            EndPoint.JSON.forward(req, res, route, controller, args);
        }
    }

    private Route findRoute(final HttpServletRequest req) {
        final HttpMethod httpMethod = HttpMethod.httpMethodOf(req.getMethod());
        final String path = req.getServletPath();
        return router.findRoute(httpMethod, path);
    }
}

