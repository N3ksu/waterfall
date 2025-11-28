package waterfall.core.reflection.annotation.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolver for merging <b>compiled</b> annotation
 */
public final class CMergedAnnotationResolver {
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
        // getClass from Object would return a Proxy class and not the annotation's class
        Class<? extends Annotation> lowerClass = lower.annotationType();

        if (lowerClass.equals(higherClass))
            return higherClass.cast(lower);

        if (!lowerClass.isAnnotationPresent(MergeTo.class))
            return null;

        MergeTo mergeTo = lowerClass.getAnnotation(MergeTo.class);
        Class<? extends  Annotation> immediateHigherClass = mergeTo.value();
        Annotation immediateHigher = lowerClass.getAnnotation(immediateHigherClass);

        if (immediateHigher == null)
            return null;

        if (immediateHigherClass.equals(higherClass)) {
            return mergeAnnotation(higherClass, immediateHigher, lower);
        }

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
            } catch (IllegalAccessException e) {
                // Method inside Annotation are always public that's why we ignore IllegalAccessException
            } catch (InvocationTargetException e) {
                /*
                    Only a custom dynamic proxy can cause this exception, because InvocationTargetException is a wrapper for all
                    exception thrown by the method invoked with Method.invoke, and annotation method cannot throw exception by default
                */
                throw new Exception(e);
            }
        }

        Class<? extends Annotation> lowerClass = lower.annotationType();
        for (Method lowerMethod : lowerClass.getDeclaredMethods()) {
            try {
                // Check if the method is present on the higher annotation else throw NoSuchMethod Exception
                Method higherMethod = higherClass.getDeclaredMethod(lowerMethod.getName());

                // Since method inside Annotation cannot return null childValue cannot be null
                // ! The previous statement is only true if the Annotation is a compiled one and not a custom dynamic Proxy
                Object lowerValue = lowerMethod.invoke(lower);

                Class<?> higherMethodReturnType = higherMethod.getReturnType();
                Class<?> lowerMethodReturnType = lowerMethod.getReturnType();
                if (!higherMethodReturnType.isAssignableFrom(lowerMethodReturnType))
                    throw new Exception("Return type " + lowerMethodReturnType.getName() +
                            " for method " + lowerMethod.getName() + " inside " +
                            lowerClass.getName() + " cannot be assigned to parent method " +
                            higherMethod.getName() + " inside " + higherClass.getName() +
                            " with return type of " + higherMethodReturnType.getName());

                if (!isDefaultValue(lowerMethod, lowerValue))
                    mergedValues.put(higherMethod.getName(), lowerValue);
            } catch (NoSuchMethodException e) {
                // NoSuchMethodException: If the method present inside the child isn't present inside the parent we just ignore it
            } catch (IllegalAccessException e){
                // IllegalAccessException: Method inside Annotation are always public that's why we ignore IllegalAccessException
            } catch (InvocationTargetException e) {
                /*
                    Only a custom dynamic proxy can cause this exception, because InvocationTargetException is a wrapper for all
                    exception thrown by the method invoked with Method.invoke, and annotation method cannot throw exception by default
                */
                throw new Exception(e);
            }
        }

        return (T) Proxy.newProxyInstance(
                higherClass.getClassLoader(),
                new Class<?>[] { higherClass },
                new CMergedAnnotationInvocationHandler(higherClass, mergedValues)
        );
    }

    private static boolean isDefaultValue(Method method, Object value) {
        Object defaultValue = method.getDefaultValue();

        // If the default value clause isn't written on the method
        if (defaultValue == null)
            return false;

        // ! This is okay because we work with compiled annotation so value cannot be null
        if (value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();

            if (componentType.isPrimitive()) { // This looks pretty ugly ngl, but it works

                if (componentType == int.class)
                    return Arrays.equals((int[]) defaultValue, (int[]) value);

                else if (componentType == long.class)
                    return Arrays.equals((long[]) defaultValue, (long[]) value);

                else if (componentType == short.class)
                    return Arrays.equals((short[]) defaultValue, (short[]) value);

                else if (componentType == byte.class)
                    return Arrays.equals((byte[]) defaultValue, (byte[]) value);

                else if (componentType == char.class)
                    return Arrays.equals((char[]) defaultValue, (char[]) value);

                else if (componentType == boolean.class)
                    return Arrays.equals((boolean[]) defaultValue, (boolean[]) value);

                else if (componentType == float.class)
                    return Arrays.equals((float[]) defaultValue, (float[]) value);

                else if (componentType == double.class)
                    return Arrays.equals((double[]) defaultValue, (double[]) value);

            } else {
                return Arrays.equals((Object[]) defaultValue, (Object[]) value);
            }
        }

        return defaultValue.equals(value);
    }
}
