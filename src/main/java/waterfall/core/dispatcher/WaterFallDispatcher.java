package waterfall.core.dispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.component.http.HttpMethod;
import waterfall.core.reflection.args.ArgumentResolver;
import waterfall.core.route.Route;
import waterfall.core.router.Router;
import waterfall.core.reflection.ReflectionUtil;
import waterfall.component.ui.ModelView;
import waterfall.core.destination.Destination;

public final class WaterFallDispatcher {
    private final Router router;
    private final ArgumentResolver argumentResolver;

    public WaterFallDispatcher(Router router) {
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
            Destination.MODEL_VIEW.forward(route, args, controller, req, res);
        } else if (returnType.equals(String.class)) {
            Destination.STRING.forward(route, args, controller, req, res);
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
