package waterfall.kernel.meta.proxy;

import waterfall.kernel.exception.technical.meta.MergedAnnotationLSPException;
import waterfall.kernel.meta.annotation.Extends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class MergedAnnotationResolver {
    public static <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement, Class<T> annotationClass) {
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            T merged = resolveHigherAnnotation(annotation, annotationClass);

            if (merged != null)
                return merged;
        }

        return null;
    }

    public static <T extends Annotation> T resolveHigherAnnotation(Annotation lower, Class<T> higherClass) {
        Class<? extends Annotation> lowerClass = lower.annotationType();

        if (lowerClass.equals(higherClass))
            return higherClass.cast(lower);

        if (!lowerClass.isAnnotationPresent(Extends.class))
            return null;

        Extends meta = lowerClass.getAnnotation(Extends.class);
        Class<? extends  Annotation> immediateHigherClass = meta.value();
        Annotation immediateHigher = lowerClass.getAnnotation(immediateHigherClass);

        if (immediateHigher == null)
            return null;

        if (immediateHigherClass.equals(higherClass))
            return mergeAnnotation(higherClass, immediateHigher, lower);

        Annotation merged = mergeAnnotation(immediateHigherClass, immediateHigher, lower);

        return resolveHigherAnnotation(merged, higherClass);
    }

    private static <T extends Annotation> T mergeAnnotation(Class<T> higherClass, Annotation higher, Annotation lower) {
        Map<String, Object> mergedValues = new HashMap<>();

        for (Method method : higherClass.getDeclaredMethods()) {
            try {
                Object value = method.invoke(higher);
                mergedValues.put(method.getName(), value);
            } catch (IllegalAccessException ignored) {
                // Method inside an annotation shouldn't be a private
            } catch (InvocationTargetException ignored) {
                // Method inside an annotation cannot throw a Throwable
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
                    throw new MergedAnnotationLSPException(lowerMethod, higherMethod);

                mergedValues.put(higherMethod.getName(), lowerValue);
            } catch (NoSuchMethodException ignored) {
                // Method only present in the lower annotation are ignored
            } catch (IllegalAccessException ignored) {
                // Method inside an annotation shouldn't be a private
            } catch (InvocationTargetException ignored) {
                // Method inside an annotation cannot throw a Throwable
            }
        }

        Object merged = Proxy.newProxyInstance(
            higherClass.getClassLoader(),
            new Class<?>[] { higherClass },
            new MergedAnnotationInvocationHandler(higherClass, mergedValues)
        );

        return higherClass.cast(merged);
    }
}
