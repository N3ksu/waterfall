package waterfall.kernel.meta.util;

import waterfall.kernel.exception.technical.meta.NoArgsConstructorException;
import waterfall.kernel.constant.Constant;
import waterfall.kernel.meta.proxy.MergedAnnotationResolver;
import waterfall.kernel.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    private static final MergedAnnotationResolver RESOLVER = new MergedAnnotationResolver();

    /**
     * If the setter wasn't defined this method return null
     */
    public static Method findSetter(Field field) {
        String fieldName = field.getName();
        String setterName = Constant.Reflection.SETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

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

        String prefix = Constant.Reflection.GETTER_PREFIX;

        if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType))
            prefix = Constant.Reflection.BOOLEAN_GETTER_PREFIX;

        String getterName = prefix + Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

        try {
            return field.getDeclaringClass().getDeclaredMethod(getterName);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findAnnotatedMethod(Class<?> clazz, Class<T> annotationClass) {
        Set<Pair<Method, T>> pairs = new HashSet<>();

        T annotation;
        for (Method method : clazz.getDeclaredMethods())
            if ((annotation = RESOLVER.findAnnotation(method, annotationClass)) != null)
                pairs.add(new Pair<>(method, annotation));

        return pairs;
    }

    /**
     * The no args constructor of clazz should be declared and public
     */
    public static Object newInstanceFromNoArgsConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new NoArgsConstructorException(clazz, e);
        }
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
