package waterfall.kernel.meta.util;

import waterfall.kernel.constant.Constant;
import waterfall.kernel.meta.proxy.MergedAnnotationResolver;
import waterfall.kernel.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    /**
     * If the setter wasn't defined this method return null
     */
    public static Method findSetter(final Field field) {
        final String fieldName = field.getName();
        final String setterName = Constant.Reflection.SETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

        try {
            return field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
        } catch (final NoSuchMethodException ignored) {
            return null;
        }
    }

    /**
     * If the getter wasn't defined this method return null
     */
    public static Method findGetter(final Field field) {
        final String fieldName = field.getName();
        final String getterName = Constant.Reflection.GETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

        try {
            return field.getDeclaringClass().getDeclaredMethod(getterName);
        } catch (final NoSuchMethodException ignored) {
            return null;
        }
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairs(final Class<?> c, final Class<T> a)
            throws Exception {
        final Set<Pair<Method, T>> pairs = new HashSet<>();

        T annotation;
        for (final Method method : c.getDeclaredMethods())
            if ((annotation = MergedAnnotationResolver.findAnnotation(method, a)) != null)
                pairs.add(Pair.of(method, annotation));

        return pairs;
    }

    /**
     * The no args constructor should be public
     */
    public static Object newInstanceFromNoArgsConstructor(final Class<?> c)
            throws Exception {
        final Constructor<?> ctr = c.getDeclaredConstructor();
        return ctr.newInstance();
    }
}
