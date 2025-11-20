package waterfall.core.reflection;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.component.annotation.RequestParam;
import waterfall.core.route.Route;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public final class ArgumentResolver {
    public Object[] resolve(Route route, HttpServletRequest req) throws Exception {
        Method method = route.getMethod();
        String path = req.getServletPath();
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];
        Map<String, String> pathVariables  = route.getPathVariables(path);

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            String paramName = param.getName();

            if (pathVariables.containsKey(paramName)) {
                String pathVariableValue = pathVariables.get(paramName);
                // TODO The framework only supports String for now
                Object value = pathVariableValue;
                args[i] = value;
                continue;
            } else if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam reqParam = param.getAnnotation(RequestParam.class);
                String paramValue = req.getParameter(reqParam.name());
                // TODO The framework only supports String for now
                Object value = paramValue;
                args[i] = value;
                continue;
            } else if (req.getParameter(paramName) != null) {
                String paramValue = req.getParameter(paramName);
                // TODO The framework only supports String for now
                Object value = paramValue;
                args[i] = value;
                continue;
            }

            args[i] = null;
        }

        return args;
    }
}
