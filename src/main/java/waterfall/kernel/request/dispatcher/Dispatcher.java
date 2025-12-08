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

    public Dispatcher(Router router) {
        this.router = router;
        routeArgumentResolver = new RouteArgumentResolver();
    }

    public void forward(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Route route;
        if ((route = findRoute(req)) == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, req.getServletPath());
            return;
        }

        Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(route.getAction().getDeclaringClass());
        Object[] args = routeArgumentResolver.resolve(route, req);
        Class<?> returnType = route.getAction().getReturnType();

        if (ModelView.class.equals(returnType)) {
            EndPoint.MV.forward(req, res, route, controller, args);
        } else if (route.produces(MediaType.APPLICATION_JSON)) {
            EndPoint.JSON.forward(req, res, route, controller, args);
        }
    }

    private Route findRoute(HttpServletRequest req) {
        HttpMethod httpMethod = HttpMethod.httpMethodOf(req.getMethod());
        String path = req.getServletPath();
        return router.findRoute(httpMethod, path);
    }
}

