package waterfall.core.dispatcher;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.component.http.HttpMethod;
import waterfall.core.reflection.ArgumentResolver;
import waterfall.core.route.Route;
import waterfall.core.router.Router;
import waterfall.core.constant.WaterFallConstant;
import waterfall.core.reflection.ReflectionUtil;
import waterfall.core.target.Target;
import waterfall.component.ui.ModelView;

import java.lang.reflect.Method;

public final class WaterFallDispatcher {
    public static void forward(HttpServletRequest req, HttpServletResponse res) throws Exception {
        ServletContext ctx = req.getServletContext();
        Router router = (Router) ctx.getAttribute(WaterFallConstant.ROUTER_CTX_ATTR_NAME);

        HttpMethod httpMethod = HttpMethod.typeOf(req.getMethod());
        String path = req.getServletPath();
        Route route = router.findRoute(httpMethod, path);

        if (route == null) {
            Target.NOT_FOUND.land(null, null, null, req, res);
            return;
        }

        // Getting an instance of the controller
        Method method = route.getMethod();
        Class<?> controllerClass = method.getDeclaringClass();
        Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(controllerClass);

        // Getting the arguments for the controller's method
        // TODO How can we deal with arrays or Collections
        Object[] args = ArgumentResolver.resolve(route, req);

        // Getting the return type of the controller's method
        Class<?> returnType = method.getReturnType();

        // TODO we can improve those if statement
        if (returnType.equals(ModelView.class)) {
            Target.MODEL_VIEW.land(route, args, controller, req, res);
        } else if (returnType.equals(String.class)) {
            Target.STRING.land(route, args, controller, req, res);
        } else if (returnType.equals(void.class)) {
           Target.VOID.land(route, args, controller, req, res);
        } else {
            // TODO what if the return type cannot be used by the framework
        }
    }
}
