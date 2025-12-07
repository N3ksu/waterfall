package waterfall.kernel.meta.util;

import waterfall.kernel.constant.ReflectionConstant;
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
    public static Method findSetter(Field field) {
        String fieldName = field.getName();
        String setterName = ReflectionConstant.SETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

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
        String fieldName = field.getName();
        String getterName = ReflectionConstant.GETTER_PREFIX +  Character.toUpperCase(fieldName.charAt(0)) + field.getName().substring(1);

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
}
