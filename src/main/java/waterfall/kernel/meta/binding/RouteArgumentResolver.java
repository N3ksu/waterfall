package waterfall.kernel.meta.binding;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import waterfall.api.annotation.request.RequestParam;
import waterfall.api.io.UploadedFile;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.serialization.string.StringUnMarshaller;
import waterfall.kernel.routing.route.Route;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RouteArgumentResolver {
    private final RouteModelBinder routeModelBinder;

    public RouteArgumentResolver() {
        routeModelBinder = new RouteModelBinder();
    }

    public Object[] resolve(Route route, HttpServletRequest request) throws Exception {
        Parameter[] params = route.getAction().getParameters();
        Object[] args = new Object[params.length];

        Map<String, String> pathVars =  route.getPathVariables(request.getServletPath());

        String pathVarValue;
        String[] paramStrValues;

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (isParameterMapStringKeyStringArrayValue(param)) {
                args[i] = request.getParameterMap();
                continue;
            } else if (isParameterMapStringKeyListUploadedFileValue(param)) {
                args[i] = getRequestUploadedFiles(request);
                continue;
            } else if ((pathVarValue = pathVars.get(param.getName())) != null) {
                // What if the client is dumb enough to use int[] id as a parameter
                args[i] = StringUnMarshaller.unMarshal(pathVarValue, param.getType());
                continue;
            } else if ((paramStrValues = request.getParameterValues(getRequestParameterName(param))) != null) {
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
                        List<String> dotNotations = findDotNotations(request, param);
                        Object model = ReflectionUtil.newInstanceFromNoArgsConstructor(param.getType());

                        for (String dotNotation : dotNotations)
                            routeModelBinder.bind(model, dotNotation.split("\\."), 1, request.getParameter(dotNotation));

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
    
    private static String getRequestParameterName(Parameter parameter) {
        return  parameter.isAnnotationPresent(RequestParam.class) ?
                parameter.getAnnotation(RequestParam.class).value() :
                parameter.getName();
    }

    private static Map<String, List<UploadedFile>> getRequestUploadedFiles(HttpServletRequest request) throws Exception {
        Map<String, List<UploadedFile>> uploads = new HashMap<>();

        for (Part part : request.getParts()) {
            String partName = part.getName();
            uploads.computeIfAbsent(partName, ignored -> new ArrayList<>());
            uploads.get(partName).add(UploadedFile.of(part));
        }

        return uploads;
    }

    private static boolean isParameterMapStringKeyStringArrayValue(Parameter parameter) {
        Type type = parameter.getParameterizedType();

        if (!(type instanceof ParameterizedType mapType))
            return false;

        if (mapType.getRawType() != Map.class)
            return false;

        Type[] mapArgs = mapType.getActualTypeArguments();

        if (mapArgs[0] != String.class)
            return false;

        return mapArgs[1] instanceof Class<?> arrayType && arrayType.getComponentType() == String.class;
    }

    private static boolean isParameterMapStringKeyListUploadedFileValue(Parameter param) {
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

        return listArgs[0] instanceof Class<?> clazz && clazz == UploadedFile.class;
    }
}
