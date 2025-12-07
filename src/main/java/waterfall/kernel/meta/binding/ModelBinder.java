package waterfall.kernel.meta.binding;

import waterfall.kernel.constant.RegexConstant;
import waterfall.kernel.meta.util.ReflectionUtil;
import waterfall.kernel.util.string.StringToObjectConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Matcher;

public final class ModelBinder {
    private final StringToObjectConverter stringToObjectConverter;

    public ModelBinder() {
        stringToObjectConverter = new StringToObjectConverter();
    }

    public void bind(Object model, String[] fieldTree, int f, String strValue) throws Exception {
        Matcher matcher = RegexConstant.ARRAY_NOTATION_PATTERN.matcher(fieldTree[f]);

        if (!matcher.matches())
            throw new Exception("Invalid field " + fieldTree[f]);

        String fieldName = matcher.group(1);
        int i = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : -1;

        Field field = model.getClass().getDeclaredField(fieldName);
        Method setter = ReflectionUtil.findSetter(field);
        Method getter = ReflectionUtil.findGetter(field);

        if (setter != null && getter != null) {
            Type fieldGenericType = field.getGenericType();

            if (fieldGenericType instanceof Class<?> clazz)
                if (!clazz.isArray() && i < 0) {
                    if (stringToObjectConverter.isConvertible(clazz))
                        setter.invoke(model, stringToObjectConverter.convert(strValue, clazz));
                    else {
                        Object subModel = ReflectionUtil.newInstanceFromNoArgsConstructor(clazz);
                        bind(subModel, fieldTree, f + 1, strValue);
                        setter.invoke(model, subModel);
                    }
                } else {
                    Class<?> componentType = clazz.getComponentType();

                    if (stringToObjectConverter.isConvertible(componentType)) {
                        Object element = stringToObjectConverter.convert(strValue, componentType);
                        Object array = addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        setter.invoke(model, array);
                    } else {
                        Object element = ReflectionUtil.newInstanceFromNoArgsConstructor(componentType);
                        Object array = addToArrayOrReplace(getter.invoke(model), i, element, componentType);
                        bind(element, fieldTree, f + 1, strValue);
                        setter.invoke(model, array);
                    }
                }
        }
    }

    public Object addToArrayOrReplace(Object array, int i, Object element, Class<?> elementClass) {
        if (array == null) {
            Object newArray  = Array.newInstance(elementClass, i + 1);
            Array.set(newArray, i, element);
            return newArray;
        }

        int length = Array.getLength(array);
        int newLength = Math.max(i + 1, length);

        if (newLength > length) {
            Object newArray = Array.newInstance(elementClass, newLength);

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
