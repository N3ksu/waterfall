package waterfall.core.reflection.args;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.component.annotation.request.RequestParam;
import waterfall.core.route.Route;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public final class ArgumentResolver {
    private final ArgumentParser argumentParser;

    public ArgumentResolver() {
        argumentParser = new ArgumentParser();
    }

    public Object[] resolve(Route route, HttpServletRequest req) throws Exception {
        Parameter[] params = route.getMethod().getParameters();
        Object[] args = new Object[params.length];

        Map<String, String> pathVariables =  route.getPathVariables(req.getServletPath());
        String pathVarValue;
        String[] paramValues;

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Class<?> paramType = param.getType();

            if (isParamMapStringStringArray(param)) {
                args[i] = req.getParameterMap();
                continue;
            } else if ((pathVarValue = pathVariables.get(param.getName())) != null) {
                // what if the client is dumb enough to use int[] id as a parameter
                args[i] = argumentParser.parseString(pathVarValue, paramType);
                continue;
            } else if ((paramValues = req.getParameterValues(getRequestParameterName(param))) != null) {
                if (paramType.isArray()) {
                    Class<?> componentType = paramType.getComponentType();
                    Object array = Array.newInstance(componentType, paramValues.length);

                    for (int j = 0; j < paramValues.length; j++)
                        Array.set(array, j, argumentParser.parseString(paramValues[j], componentType));

                    args[i] = array;
                } else
                    args[i] = paramValues.length > 0 ? argumentParser.parseString(paramValues[0], paramType) : null;

                continue;
            }

            args[i] = null;
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
        if (args.length != 2)
            return false;

        Type keyType = args[0];
        Type valueType = args[1];

        boolean keyIsString = keyType == String.class;
        boolean valueIsStringArray = false;

        if (valueType instanceof Class<?> clazz && clazz.isArray())
            valueIsStringArray = clazz.getComponentType() == String.class;

        return keyIsString && valueIsStringArray;
    }
}
