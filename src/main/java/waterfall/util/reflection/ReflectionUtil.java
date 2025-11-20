package waterfall.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class ReflectionUtil {
    public static Set<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> methodsAnnotationClass) {
        Set<Method> methods = new HashSet<>();

        for(Method method : clazz.getDeclaredMethods())
            if(method.isAnnotationPresent(methodsAnnotationClass))
                methods.add(method);

        return methods;
    }

    public static Object newInstanceFromNoArgsConstructor(Class<?> c)
            throws Exception {
        Constructor<?> ctr = c.getDeclaredConstructor();
        ctr.setAccessible(true); // ! making the constructor public
        return ctr.newInstance();
    }
}
