package waterfall.dispatcher;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.constant.WaterFallConstant;
import waterfall.reflection.ReflectionUtil;
import waterfall.target.Target;
import waterfall.bootstrap.net.route.Route;
import waterfall.bootstrap.net.router.Router;
import waterfall.ui.ModelView;

import java.lang.reflect.Method;

public class WaterFallDispatcher implements IWaterFallDispatcher {
    @Override
    public void forward(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String path = req.getServletPath();
        ServletContext ctx = req.getServletContext();
        Router router = (Router) ctx.getAttribute(WaterFallConstant.ROUTER_CTX_ATTR_NAME);

        Route route = router.findRoute(path);

        if (route == null) {
            Target.NOT_FOUND.land(null, null, req, res);
            return;
        }

        // Getting an instance of the controller
        Method method = route.getMethod();
        Object controller = ReflectionUtil.newInstanceFromNoArgsConstructor(method.getDeclaringClass());

        Class<?> returnType = method.getReturnType();

        // TODO we can improve those if statement
        if (returnType.equals(ModelView.class)) {
            Target.MODEL_VIEW.land(route, controller, req, res);
        } else if (returnType.equals(String.class)) {
            Target.STRING.land(route, controller, req, res);
        } else if (returnType.equals(void.class)) {
           Target.VOID.land(route, controller, req, res);
        } else {
            // TODO what if the return type cannot be used by the framework
        }
    }
}
