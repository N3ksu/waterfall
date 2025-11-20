package waterfall.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtil {
    public static Object newInstanceFromNoArgsConstructor(Class<?> c)
            throws Exception {
        Constructor<?> ctr = c.getDeclaredConstructor();
        ctr.setAccessible(true); // ! making the constructor public
        return ctr.newInstance();
    }
}
