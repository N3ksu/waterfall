package waterfall.kernel.meta.binding;

import jakarta.servlet.http.HttpServletRequest;
import waterfall.api.annotation.request.RequestParam;
import waterfall.kernel.constant.Constant;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.serialization.string.StringUnMarshaller;
import waterfall.kernel.routing.route.Route;
import waterfall.kernel.util.tuple.Pair;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteArgumentResolver {
    public static Object[] resolve(Route route, HttpServletRequest req) throws Exception {
        Parameter[] params = route.getAction().getParameters();
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
                args[i] = StringUnMarshaller.unMarshal(pathVarValue, param.getType());
                continue;
            } else if ((paramStrValues = req.getParameterValues(getRequestParameterName(param))) != null) {
                Class<?> paramType = param.getType();

                if (paramType.isArray()) args[i] = StringUnMarshaller.unMarshal(paramStrValues, paramType.getComponentType());
                else args[i] = paramStrValues.length > 0 ?
                            StringUnMarshaller.unMarshal(paramStrValues[0], paramType) :
                            null; // TODO provide better default value
                continue;
            } else {
                Type parameterizedType = param.getParameterizedType();

                if (parameterizedType instanceof Class<?> clazz)
                    if (!clazz.isArray()) {
                        List<String> dotNotations = findDotNotations(req, param);
                        Object model = ReflectionUtil.newInstanceFromNoArgsConstructor(param.getType());

                        for (String dotNotation : dotNotations)
                            RouteModelBinder.bind(model, dotNotation.split("\\."), 1, req.getParameter(dotNotation));

                        args[i] = model;
                        continue;
                    } else {
                        List<Pair<String, Integer>> dotNotations = findArrayDotNotations(req, param);

                        for (Pair<String, Integer> dotNotation : dotNotations) {
                            
                        }
                    }
            }
            args[i] = null; // TODO provide better default value
        }

        return args;
    }

    private static List<String> findDotNotations(HttpServletRequest req, Parameter param) {
        return req.getParameterMap()
                .keySet()
                .stream()
                .filter(s -> s.startsWith(param.getName() + "."))
                .toList();
    }

    private static List<Pair<String, Integer>> findArrayDotNotations(HttpServletRequest req ,Parameter param) {
        Pattern pattern = Pattern.compile("^" + param.getName() + "\\[(\\d+)](?:\\..*)?$");
        List<Pair<String, Integer>> dotNotations = new ArrayList<>();

        for (String key : req.getParameterMap().keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                int i = Integer.parseInt(matcher.group(1));
                dotNotations.add(Pair.of(key, i));
            }
        }

        return dotNotations;
    }
    
    private static String getRequestParameterName(Parameter param) {
        return  param.isAnnotationPresent(RequestParam.class) ?
                param.getAnnotation(RequestParam.class).value() :
                param.getName();
    }

    private static boolean isParamMapStringStringArray(Parameter param) {
        final Type type = param.getParameterizedType();

        if (!(type instanceof ParameterizedType parameterizedType)) return false;

        if (parameterizedType.getRawType() != Map.class) return false;

        final Type[] args = parameterizedType.getActualTypeArguments();
        final boolean keyIsString = args[0] == String.class;
        boolean valueIsStringArray = false;

        if (args[1] instanceof Class<?> clazz && clazz.isArray()) valueIsStringArray = clazz.getComponentType() == String.class;

        return keyIsString && valueIsStringArray;
    }
}
