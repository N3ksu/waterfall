package waterfall.kernel.meta.binding;

import waterfall.kernel.constant.Constant;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.serialization.string.StringUnMarshaller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RouteModelBinder {
    private static final Pattern ARRAY_DOT_NOTATION_PATTERN_OPTIONAL_INDEX = Pattern.compile
            ("^(" + Constant.Regex.JAVA_VAR_NOMENCLATURE_REGEX + ")(?:\\[(\\d+)])?$");

    public void bind(Object model, String[] fieldTree, int f, String strValue) throws Exception {
        if (f >= fieldTree.length) return;

        Matcher matcher = ARRAY_DOT_NOTATION_PATTERN_OPTIONAL_INDEX.matcher(fieldTree[f]);

        if (!matcher.matches()) throw new Exception("Invalid field " + fieldTree[f]);

        String fieldName = matcher.group(1);
        int i = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : -1;

        Field field = model.getClass().getDeclaredField(fieldName);
        Method setter = ReflectionUtil.findSetter(field);
        Method getter = ReflectionUtil.findGetter(field);

        if (setter != null && getter != null) {
            Type fieldGenericType = field.getGenericType();

            if (fieldGenericType instanceof Class<?> clazz)
                if (!clazz.isArray() && i < 0) {
                    if (StringUnMarshaller.isSupported(clazz)) setter.invoke(model, StringUnMarshaller.unMarshal(strValue, clazz));

                    else {
                        Object subModel = ReflectionUtil.newInstanceFromNoArgsConstructor(clazz);
                        bind(subModel, fieldTree, f + 1, strValue);
                        setter.invoke(model, subModel);
                    }
                } else {
                    Class<?> componentType = clazz.getComponentType();

                    if (StringUnMarshaller.isSupported(componentType)) {
                        Object element = StringUnMarshaller.unMarshal(strValue, componentType);
                        Object array = ReflectionUtil.addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        setter.invoke(model, array);
                    } else {
                        Object element = ReflectionUtil.newInstanceFromNoArgsConstructor(componentType);
                        Object array = ReflectionUtil.addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        bind(element, fieldTree, f + 1, strValue);
                        setter.invoke(model, array);
                    }
                }
        }
    }
}
