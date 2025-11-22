package waterfall.core.reflection;

import waterfall.core.reflection.annotation.proxy.CMergedAnnotationResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

public final class ReflectionUtil {
    public static Set<Method> findAnnotatedMethods(Class<?> c, Class<? extends Annotation> a) {
        Set<Method> methods = new HashSet<>();

        for (Method method : c.getDeclaredMethods())
            if(method.isAnnotationPresent(a))
                methods.add(method);

        return methods;
    }

    public static <T extends Annotation> Set<SimpleEntry<Method, T>> findAnnotatedMethodEntries(Class<?> c, Class<T> a)
            throws Exception {
        Set<SimpleEntry<Method, T>> entries = new HashSet<>();

        T annotation;
        for (Method method : c.getDeclaredMethods())
            if ((annotation = CMergedAnnotationResolver.findAnnotation(method, a)) != null)
                entries.add(new SimpleEntry<>(method, annotation));

        return entries;
    }

    public static Object newInstanceFromNoArgsConstructor(Class<?> c)
            throws Exception {
        Constructor<?> ctr = c.getDeclaredConstructor();
        ctr.setAccessible(true); // ! making the constructor public
        return ctr.newInstance();
    }
}
