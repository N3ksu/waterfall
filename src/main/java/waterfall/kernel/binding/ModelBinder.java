package waterfall.kernel.binding;

import waterfall.kernel.constant.WFConstant;
import waterfall.kernel.reflection.ReflectionUtil;
import waterfall.kernel.util.parser.StringParser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModelBinder {
    private final StringParser stringParser;
    private final Pattern arrayNotationPattern;

    public ModelBinder() {
        stringParser = new StringParser();
        arrayNotationPattern = Pattern.compile(WFConstant.ARRAY_NOTATION_RGX);
    }

    public void bind(Object model, String[] fieldTree, int f, String strValue) throws Exception {
        Matcher matcher = arrayNotationPattern.matcher(fieldTree[f]);

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
                    if (stringParser.isSupported(clazz))
                        setter.invoke(model, stringParser.parseString(strValue, clazz));
                    else {
                        Object subModel = ReflectionUtil.newInstanceFromNoArgsConstructor(clazz);
                        bind(subModel, fieldTree, f + 1, strValue);
                        setter.invoke(model, subModel);
                    }
                } else {
                    Class<?> componentType = clazz.getComponentType();

                    if (stringParser.isSupported(componentType)) {
                        Object element = stringParser.parseString(strValue, componentType);
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
