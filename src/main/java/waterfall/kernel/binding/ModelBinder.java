package waterfall.kernel.binding;

import waterfall.kernel.reflection.ReflectionUtil;
import waterfall.kernel.util.parser.StringParser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ModelBinder {
    private final StringParser stringParser;

    public ModelBinder() {
        stringParser = new StringParser();
    }

    public void bind(Object model, String[] fieldTree, int f, String[] strValues, int v) throws Exception {
        Field field = model.getClass().getDeclaredField(fieldTree[f]);
        Method setter = ReflectionUtil.findSetter(field);

        if (setter != null) {
            setter.setAccessible(true);
            Type fieldGenericType = field.getGenericType();

            if (fieldGenericType instanceof Class<?> clazz)
                if (!clazz.isArray())
                    if (stringParser.isSupported(clazz))
                        setter.invoke(model, stringParser.parseString(strValues[v], clazz));
                    else
                        bind(ReflectionUtil.newInstanceFromNoArgsConstructor(clazz), fieldTree, f + 1, strValues, v);
                else {
                    Class<?> componentType = clazz.getComponentType();

                    if (stringParser.isSupported(componentType))
                        setter.invoke(model, (Object) stringParser.parseStringArray(strValues, componentType));

                    else {
                        Object array = Array.newInstance(componentType, strValues.length);

                        for (int i = 0; i < strValues.length; i++) {
                            Array.set(array, i, ReflectionUtil.newInstanceFromNoArgsConstructor(componentType));
                            bind(Array.get(array, i), fieldTree, f + 1, strValues, i);
                        }
                        setter.invoke(model, array);
                    }
                }
        }
    }
}
