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
    public static <T extends Annotation> T findAnnotation(final AnnotatedElement annotatedElement, final Class<T> annotationClass)
            throws Exception {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            final T composed = resolveHigherAnnotation(annotation, annotationClass);
            if (composed != null) return composed;
        }

        return null;
    }

    public static <T extends Annotation> T resolveHigherAnnotation(final Annotation lower, final Class<T> higherClass)
            throws Exception {
        final Class<? extends Annotation> lowerClass = lower.annotationType();

        if (lowerClass.equals(higherClass)) return higherClass.cast(lower);

        if (!lowerClass.isAnnotationPresent(Extends.class)) return null;

        final Extends meta = lowerClass.getAnnotation(Extends.class);
        final Class<? extends  Annotation> immediateHigherClass = meta.value();
        final Annotation immediateHigher = lowerClass.getAnnotation(immediateHigherClass);

        if (immediateHigher == null) return null;

        if (immediateHigherClass.equals(higherClass)) return mergeAnnotation(higherClass, immediateHigher, lower);

        final Annotation merged = mergeAnnotation(immediateHigherClass, immediateHigher, lower);

        return resolveHigherAnnotation(merged, higherClass);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T mergeAnnotation(final Class<T> higherClass, final Annotation higher, final Annotation lower)
            throws Exception {
        final Map<String, Object> mergedValues = new HashMap<>();

        for (final Method method : higherClass.getDeclaredMethods()) {
            try {
                final Object value = method.invoke(higher);
                mergedValues.put(method.getName(), value);
            } catch (final IllegalAccessException ignored) {
            } catch (final InvocationTargetException e) {
                throw new Exception(e);
            }
        }

        final Class<? extends Annotation> lowerClass = lower.annotationType();
        for (final Method lowerMethod : lowerClass.getDeclaredMethods()) {
            try {
                final Method higherMethod = higherClass.getDeclaredMethod(lowerMethod.getName());
                final Object lowerValue = lowerMethod.invoke(lower);
                final Class<?> higherMethodReturnType = higherMethod.getReturnType();
                final Class<?> lowerMethodReturnType = lowerMethod.getReturnType();

                if (!higherMethodReturnType.isAssignableFrom(lowerMethodReturnType))
                    throw new Exception("Return type " + lowerMethodReturnType.getName() +
                            " for method " + lowerMethod.getName() + " inside " +
                            lowerClass.getName() + " cannot be assigned to parent method " +
                            higherMethod.getName() + " inside " + higherClass.getName() +
                            " with return type of " + higherMethodReturnType.getName());

                mergedValues.put(higherMethod.getName(), lowerValue);
            } catch (final NoSuchMethodException | IllegalAccessException ignored) {
            } catch (final InvocationTargetException e) {
                throw new Exception(e);
            }
        }

        return (T) Proxy.newProxyInstance(
                higherClass.getClassLoader(),
                new Class<?>[] { higherClass },
                new MergedAnnotationInvocationHandler(higherClass, mergedValues));
    }
}
