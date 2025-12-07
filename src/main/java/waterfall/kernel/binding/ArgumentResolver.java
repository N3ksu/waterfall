package waterfall.kernel.binding;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.api.annotation.request.RequestParam;
import waterfall.kernel.constant.WFConstant;
import waterfall.kernel.reflection.ReflectionUtil;
import waterfall.kernel.util.parser.StringParser;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.util.tuple.Pair;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class ArgumentResolver {
    private final StringParser stringParser;
    private final ModelBinder modelBinder;
    private final Pattern arrayNotationPattern;

    public ArgumentResolver() {
        stringParser = new StringParser();
        modelBinder = new ModelBinder();
        arrayNotationPattern = Pattern.compile(WFConstant.ARRAY_NOTATION_RGX);
    }

    public Object[] resolve(Route route, HttpServletRequest req) throws Exception {
        Parameter[] params = route.getMethod().getParameters();
        Object[] args = new Object[params.length];

        Map<String, String> pathVars =  route.getPathVariables(req.getServletPath());

        String pathVarValue;
        String[] paramStrValues;

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
            } else {
                Type parameterizedType = param.getParameterizedType();

                if (parameterizedType instanceof Class<?> clazz)
                    if (!clazz.isArray()) {
                        List<String> dotNotations = findDotNotations(req, param);

                        Object model = ReflectionUtil.newInstanceFromNoArgsConstructor(param.getType());

                        for (String dotNotation : dotNotations)
                            modelBinder.bind(model, dotNotation.split("\\."), 1, req.getParameter(dotNotation));

                        args[i] = model;
                        continue;
                    } else {

                    }
            }
            args[i] = null; // TODO provide better default value
        }

        return args;
    }

    public List<String> findDotNotations(HttpServletRequest req, Parameter param) {
        return req.getParameterMap()
                .keySet()
                .stream()
                .filter(s -> s.startsWith(param.getName() + "."))
                .toList();
    }

    public List<Pair<String, Integer>> findDotArrayNotations(HttpServletRequest req ,Parameter param) {
        return null;
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
