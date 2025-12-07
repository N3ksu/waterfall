package waterfall.kernel.meta.proxy;

import waterfall.kernel.meta.annotation.Extends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class MergedAnnotationResolver {
    public static <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement, Class<T> annotationClass)
            throws Exception {
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            T composed = resolveHigherAnnotation(annotation, annotationClass);
            if (composed != null)
                return composed;
        }

        return null;
    }

    public static <T extends Annotation> T resolveHigherAnnotation(Annotation lower, Class<T> higherClass)
            throws Exception {
        Class<? extends Annotation> lowerClass = lower.annotationType();

        if (lowerClass.equals(higherClass))
            return higherClass.cast(lower);

        if (!lowerClass.isAnnotationPresent(Extends.class))
            return null;

        Extends mergeTo = lowerClass.getAnnotation(Extends.class);
        Class<? extends  Annotation> immediateHigherClass = mergeTo.value();
        Annotation immediateHigher = lowerClass.getAnnotation(immediateHigherClass);

        if (immediateHigher == null)
            return null;

        if (immediateHigherClass.equals(higherClass))
            return mergeAnnotation(higherClass, immediateHigher, lower);

        Annotation merged = mergeAnnotation(immediateHigherClass, immediateHigher, lower);

        return resolveHigherAnnotation(merged, higherClass);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T mergeAnnotation(Class<T> higherClass, Annotation higher, Annotation lower)
            throws Exception {
        Map<String, Object> mergedValues = new HashMap<>();

        for (Method method : higherClass.getDeclaredMethods()) {
            try {
                Object value = method.invoke(higher);
                mergedValues.put(method.getName(), value);
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException e) {
                throw new Exception(e);
            }
        }

        Class<? extends Annotation> lowerClass = lower.annotationType();
        for (Method lowerMethod : lowerClass.getDeclaredMethods()) {
            try {
                Method higherMethod = higherClass.getDeclaredMethod(lowerMethod.getName());
                Object lowerValue = lowerMethod.invoke(lower);
                Class<?> higherMethodReturnType = higherMethod.getReturnType();
                Class<?> lowerMethodReturnType = lowerMethod.getReturnType();

                if (!higherMethodReturnType.isAssignableFrom(lowerMethodReturnType))
                    throw new Exception("Return type " + lowerMethodReturnType.getName() +
                            " for method " + lowerMethod.getName() + " inside " +
                            lowerClass.getName() + " cannot be assigned to parent method " +
                            higherMethod.getName() + " inside " + higherClass.getName() +
                            " with return type of " + higherMethodReturnType.getName());

                mergedValues.put(higherMethod.getName(), lowerValue);
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
            } catch (InvocationTargetException e) {
                throw new Exception(e);
            }
        }

        return (T) Proxy.newProxyInstance(
                higherClass.getClassLoader(),
                new Class<?>[] { higherClass },
                new MergedAnnotationInvocationHandler(higherClass, mergedValues));
    }
}
