package waterfall.core.dispatcher;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.reflection.ArgumentResolver;
import waterfall.core.route.Route;
import waterfall.core.router.Router;
import waterfall.constant.WaterFallConstant;
import waterfall.util.reflection.ReflectionUtil;
import waterfall.core.target.ITarget;
import waterfall.component.ui.ModelView;

import java.lang.reflect.Method;

public final class WaterFallDispatcher {
    public static void forward(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String path = req.getServletPath();
        ServletContext ctx = req.getServletContext();
        Router router = (Router) ctx.getAttribute(WaterFallConstant.ROUTER_CTX_ATTR_NAME);

        Route route = router.findRoute(path);

        if (route == null) {
            ITarget.NOT_FOUND.land(null, null, null, req, res);
            return;
        }

        // Getting an instance of the controller
        Method method = route.getMethod();
        Class<?> controllerClass = method.getDeclaringClass();
        Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(controllerClass);

        // Getting the arguments for the controller's method
        // TODO How can we deal with arrays ou Collection
        Object[] args = ArgumentResolver.resolve(route, req);

        // Getting the return type of the controller's method
        Class<?> returnType = method.getReturnType();

        // TODO we can improve those if statement
        if (returnType.equals(ModelView.class)) {
            ITarget.MODEL_VIEW.land(route, args, controller, req, res);
        } else if (returnType.equals(String.class)) {
            ITarget.STRING.land(route, args, controller, req, res);
        } else if (returnType.equals(void.class)) {
           ITarget.VOID.land(route, args, controller, req, res);
        } else {
            // TODO what if the return type cannot be used by the framework
        }
    }
}
