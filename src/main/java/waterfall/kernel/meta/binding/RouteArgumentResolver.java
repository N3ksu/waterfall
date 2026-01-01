package waterfall.kernel.meta.binding;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import waterfall.api.annotation.request.RequestParam;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.serialization.string.StringUnMarshaller;
import waterfall.kernel.routing.route.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            } else if (isParamMapStringListByteArray(param)) {
                args[i] = getRequestParts(req);
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
                    } else {}
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
    
    private static String getRequestParameterName(Parameter param) {
        return  param.isAnnotationPresent(RequestParam.class) ?
                param.getAnnotation(RequestParam.class).value() :
                param.getName();
    }

    private static Map<String, List<byte[]>> getRequestParts(HttpServletRequest req) throws Exception {
        return req.getParts().stream().filter(p -> p.getSubmittedFileName() != null)
                .collect(Collectors.groupingBy(Part::getName,
                        Collectors.mapping(p -> {
                                    try (InputStream in = p.getInputStream()) {
                                        return in.readAllBytes();
                                    } catch (IOException e) {
                                        throw new UncheckedIOException(e);
                                    }}, Collectors.toList())));
    }

    private static boolean isParamMapStringStringArray(Parameter param) {
        Type type = param.getParameterizedType();

        if (!(type instanceof ParameterizedType mapType))
            return false;

        if (mapType.getRawType() != Map.class)
            return false;

        Type[] mapArgs = mapType.getActualTypeArguments();

        if (mapArgs[0] != String.class)
            return false;

        return mapArgs[1] instanceof Class<?> arrayType && arrayType.getComponentType() == String.class;
    }

    private static boolean isParamMapStringListByteArray(Parameter param) {
        Type type = param.getParameterizedType();

        if (!(type instanceof ParameterizedType mapType))
            return false;

        if(mapType.getRawType() != Map.class)
            return false;

        Type[] mapArgs = mapType.getActualTypeArguments();

        if (mapArgs[0] != String.class)
            return false;

        if (!(mapArgs[1] instanceof ParameterizedType listType))
            return false;

        if (listType.getRawType() != List.class)
            return false;

        Type[] listArgs = listType.getActualTypeArguments();

        return listArgs[0] instanceof Class<?> arrayType && arrayType.getComponentType() == byte.class;
    }
}
