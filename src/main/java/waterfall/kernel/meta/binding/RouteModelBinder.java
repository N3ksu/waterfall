package waterfall.kernel.meta.binding;

import waterfall.kernel.constant.Constant;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.serialization.string.StringUnMarshaller;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Matcher;

public final class RouteModelBinder {
    public static void bind(final Object model, final String[] fieldTree, final int f, final String strValue) throws Exception {
        final Matcher matcher = Constant.Regex.ARRAY_NOTATION_PATTERN.matcher(fieldTree[f]);

        if (!matcher.matches()) throw new Exception("Invalid field " + fieldTree[f]);

        final String fieldName = matcher.group(1);
        final int i = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : -1;

        final Field field = model.getClass().getDeclaredField(fieldName);
        final Method setter = ReflectionUtil.findSetter(field);
        final Method getter = ReflectionUtil.findGetter(field);

        if (setter != null && getter != null) {
            final Type fieldGenericType = field.getGenericType();

            if (fieldGenericType instanceof Class<?> clazz)
                if (!clazz.isArray() && i < 0) {
                    if (StringUnMarshaller.isSupported(clazz)) setter.invoke(model, StringUnMarshaller.unMarshal(strValue, clazz));

                    else {
                        final Object subModel = ReflectionUtil.newInstanceFromNoArgsConstructor(clazz);
                        bind(subModel, fieldTree, f + 1, strValue);
                        setter.invoke(model, subModel);
                    }
                } else {
                    final Class<?> componentType = clazz.getComponentType();

                    if (StringUnMarshaller.isSupported(componentType)) {
                        final Object element = StringUnMarshaller.unMarshal(strValue, componentType);
                        final Object array = addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        setter.invoke(model, array);
                    } else {
                        final Object element = ReflectionUtil.newInstanceFromNoArgsConstructor(componentType);
                        final Object array = addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        bind(element, fieldTree, f + 1, strValue);
                        setter.invoke(model, array);
                    }
                }
        }
    }

    public static Object addToArrayOrReplace(final Object array, final int i, final Object element, final Class<?> elementClass) {
        if (array == null) {
            final Object newArray  = Array.newInstance(elementClass, i + 1);
            Array.set(newArray, i, element);
            return newArray;
        }

        final int length = Array.getLength(array);
        final int newLength = Math.max(i + 1, length);

        if (newLength > length) {
            final Object newArray = Array.newInstance(elementClass, newLength);

            for (int j = 0; j < length; j++)
                Array.set(newArray, j, Array.get(array, j));

            Array.set(newArray, i, element);
            return newArray;
        } else {
            Array.set(array, i, element);
            return array;
        }
    }
}
