package waterfall.core.reflection;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.component.annotation.RequestParam;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class ArgumentResolver {
    public Object[] resolve(Route route, HttpServletRequest req) {
        Method method = route.getMethod();
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];


            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam reqParam = param.getAnnotation(RequestParam.class);
                String paramValue = req.getParameter(reqParam.name());
                // TODO The framework only supports String here
                Object value = paramValue;
                args[i] = value;
                continue;
            } else if (req.getParameter(param.getName()) != null) {
                String paramValue = req.getParameter(param.getName());
                // TODO The framework only supports String here
                Object value = paramValue;
                args[i] = value;
                continue;
            }

            args[i] = null;
        }

        return args;
    }
}
