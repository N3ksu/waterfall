package waterfall.kernel.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.api.http.HttpMethod;
import waterfall.kernel.binding.ArgumentResolver;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.routing.router.Router;
import waterfall.kernel.reflection.ReflectionUtil;
import waterfall.api.ui.ModelView;
import waterfall.kernel.destination.Destination;

public final class WFDispatcher {
    private final Router router;
    private final ArgumentResolver argumentResolver;

    public WFDispatcher(Router router) {
        this.router = router;
        argumentResolver = new ArgumentResolver();
    }

    public void forward(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Route route;
        if ((route = findRoute(req)) == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, req.getServletPath());
            return;
        }

        Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(route.getMethod().getDeclaringClass());
        Object[] args = argumentResolver.resolve(route, req);
        Class<?> returnType = route.getMethod().getReturnType();

        if (returnType.equals(ModelView.class)) {
            Destination.MV.forward(route, args, controller, req, res);
        } else if (returnType.equals(String.class)) {
            Destination.STR.forward(route, args, controller, req, res);
        } else if (returnType.equals(void.class)) {
           Destination.VOID.forward(route, args, controller, req, res);
        } else {
            res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, returnType.getName());
        }
    }

    private Route findRoute(HttpServletRequest req) {
        HttpMethod httpMethod = HttpMethod.typeOf(req.getMethod());
        String path = req.getServletPath();
        return router.findRoute(httpMethod, path);
    }
}

