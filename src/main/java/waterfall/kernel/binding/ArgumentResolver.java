package waterfall.kernel.binding;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.api.annotation.request.RequestParam;
import waterfall.kernel.reflection.ReflectionUtil;
import waterfall.kernel.util.parser.StringParser;
import waterfall.kernel.routing.route.Route;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public final class ArgumentResolver {
    private final StringParser stringParser;
    private final ModelBinder modelBinder;

    public ArgumentResolver() {
        stringParser = new StringParser();
        modelBinder = new ModelBinder();
    }

    public Object[] resolve(Route route, HttpServletRequest req) throws Exception {
        Parameter[] params = route.getMethod().getParameters();
        Object[] args = new Object[params.length];

        Map<String, String> pathVars =  route.getPathVariables(req.getServletPath());

        String pathVarValue;
        String[] paramStrValues;
        List<String> dotNotations;

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (isParamMapStringStringArray(param)) {
                args[i] = req.getParameterMap();
                continue;
            } else if ((pathVarValue = pathVars.get(param.getName())) != null) {
                // What if the client is dumb enough to use int[] id as a parameter
                args[i] = stringParser.parseString(pathVarValue, param.getType());
                continue;
            } else if ((paramStrValues = req.getParameterValues(getRequestParameterName(param))) != null) {
                Class<?> paramType = param.getType();

                if (paramType.isArray())
                    args[i] = stringParser.parseStringArray(paramStrValues, paramType.getComponentType());
                else
                    args[i] = paramStrValues.length > 0 ?
                            stringParser.parseString(paramStrValues[0], paramType) :
                            null; // TODO provide better default value

                continue;
            } else if (!(dotNotations = req.getParameterMap().keySet().stream()
                    .filter(paramName -> paramName.startsWith(param.getName() + ".")).toList())
                    .isEmpty()) {
                Object model = ReflectionUtil.newInstanceFromNoArgsConstructor(param.getType());

                for (String dotNotation : dotNotations)
                    modelBinder.bind(model, dotNotation.split("\\."), 1, req.getParameterValues(dotNotation),0);

                args[i] = model;
                continue;
            }
            args[i] = null; // TODO provide better default value
        }

        return args;
    }
    
    private String getRequestParameterName(Parameter param) {
        return  param.isAnnotationPresent(RequestParam.class) ?
                param.getAnnotation(RequestParam.class).value() :
                param.getName();
    }

    private boolean isParamMapStringStringArray(Parameter param) {
        Type type = param.getParameterizedType();

        if (!(type instanceof ParameterizedType parameterizedType))
            return false;

        if (parameterizedType.getRawType() != Map.class)
            return false;

        Type[] args = parameterizedType.getActualTypeArguments();
        boolean keyIsString = args[0] == String.class;
        boolean valueIsStringArray = false;

        if (args[1] instanceof Class<?> clazz && clazz.isArray())
            valueIsStringArray = clazz.getComponentType() == String.class;

        return keyIsString && valueIsStringArray;
    }
}
