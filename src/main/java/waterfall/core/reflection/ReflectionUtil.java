package waterfall.core.reflection;

import waterfall.core.reflection.annotation.proxy.CMergedAnnotationResolver;
import waterfall.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    public static Set<Method> findAnnotatedMethods(Class<?> c, Class<? extends Annotation> a) {
        Set<Method> methods = new HashSet<>();

        for (Method method : c.getDeclaredMethods())
            if(method.isAnnotationPresent(a))
                methods.add(method);

        return methods;
    }

    public static <T extends Annotation> Set<Pair<Method, T>> findMethodAndAnnotationPairs(Class<?> c, Class<T> a)
            throws Exception {
        Set<Pair<Method, T>> pairs = new HashSet<>();

        T annotation;
        for (Method method : c.getDeclaredMethods())
            if ((annotation = CMergedAnnotationResolver.findAnnotation(method, a)) != null)
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
