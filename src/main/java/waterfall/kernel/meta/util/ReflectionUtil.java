package waterfall.kernel.meta.util;

import waterfall.kernel.meta.proxy.MergedAnnotationResolver;
import waterfall.kernel.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    public static final String SETTER_PREFIX = "set";
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";

    /**
     * If the setter wasn't defined this method return null
     */
    public static Method findSetter(Field field) {
        String fieldName = field.getName();
        String setterName = SETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

        try {
            return field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    /**
     * If the getter wasn't defined this method return null
     */
    public static Method findGetter(Field field) {
        Class<?> fieldType = field.getType();
        String fieldName = field.getName();

        String prefix = GETTER_PREFIX;

        if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) prefix = BOOLEAN_GETTER_PREFIX;

        String getterName = prefix + Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

        try {
            return field.getDeclaringClass().getDeclaredMethod(getterName);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairs(Class<?> c, Class<T> a)
            throws Exception {
        Set<Pair<Method, T>> pairs = new HashSet<>();

        T annotation;
        for (Method method : c.getDeclaredMethods())
            if ((annotation = MergedAnnotationResolver.findAnnotation(method, a)) != null)
                pairs.add(Pair.of(method, annotation));

        return pairs;
    }

    /**
     * The no args constructor should be public
     */
    public static Object newInstanceFromNoArgsConstructor(Class<?> c)
            throws Exception {
        Constructor<?> ctr = c.getDeclaredConstructor();
        return ctr.newInstance();
    }

    public static Object addToArrayOrReplace(Object array, int i, Object element, Class<?> elementClass) {
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
