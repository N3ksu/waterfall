package mg.waterfall.core.web.request.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.waterfall.core.web.routing.http.method.HttpMethod;
import mg.waterfall.core.meta.binding.RouteArgumentResolver;
import mg.waterfall.core.web.routing.http.media.MediaType;
import mg.waterfall.core.web.routing.route.Route;
import mg.waterfall.core.web.routing.router.Router;
import mg.waterfall.core.meta.util.ReflectionUtil;
import mg.waterfall.api.ui.ModelView;
import mg.waterfall.core.web.response.endpoint.EndPoint;

public final class RequestDispatcher {
    private final Router router;
    private final RouteArgumentResolver routeArgumentResolver;

    public RequestDispatcher(Router router) {
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

        if (ModelView.class.equals(route.getAction().getReturnType()))
            EndPoint.MV.forward(req, res, route, controller, args);
        else if (route.produces(MediaType.APPLICATION_JSON))
            EndPoint.JSON.forward(req, res, route, controller, args);
    }

    private Route findRoute(HttpServletRequest req) {
        HttpMethod httpMethod = HttpMethod.httpMethodOf(req.getMethod());
        String path = req.getServletPath();
        return router.findRoute(httpMethod, path);
    }
}

