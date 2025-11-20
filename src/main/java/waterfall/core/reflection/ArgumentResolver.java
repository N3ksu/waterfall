package waterfall.core.reflection;

import jakarta.servlet.http.HttpServletRequest;
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
            String value;
            if ((value = req.getParameter(param.getName())) != null) {
                args[i] = value;
                continue;
            }
            args[i] = null;
        }

        return args;
    }
}
