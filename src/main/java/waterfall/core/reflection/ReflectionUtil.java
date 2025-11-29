package waterfall.core.reflection;

import waterfall.core.reflection.annotation.proxy.MergedAnnotationResolver;
import waterfall.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairs(Class<?> c, Class<T> a)
            throws Exception {
        Set<Pair<Method, T>> pairs = new HashSet<>();

        T annotation;
        for (Method method : c.getDeclaredMethods())
            if ((annotation = MergedAnnotationResolver.findAnnotation(method, a)) != null)
                pairs.add(Pair.of(method, annotation));

        return pairs;
    }

    public static Object newInstanceFromNoArgsConstructor(Class<?> c)
            throws Exception {
        Constructor<?> ctr = c.getDeclaredConstructor();
        ctr.setAccessible(true); // ! making the constructor public
        return ctr.newInstance();
    }
}
