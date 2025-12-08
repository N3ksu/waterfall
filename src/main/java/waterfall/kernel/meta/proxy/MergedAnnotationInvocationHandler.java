package waterfall.kernel.meta.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public final class MergedAnnotationInvocationHandler implements InvocationHandler {
    private final Class<? extends Annotation> annotationClass;
    private final Map<String, Object> annotationMethodValues;

    public MergedAnnotationInvocationHandler(final Class<? extends Annotation> hightestAnnotationClass, final Map<String, Object> composedAnnotationMethodsValues) {
        this.annotationClass = hightestAnnotationClass;
        this.annotationMethodValues = composedAnnotationMethodsValues;
    }

    @Override
    public Object invoke(final Object o, final Method method, final Object[] objects) throws Throwable {
        final String methodName = method.getName();

        switch (methodName) {
            case "annotationType" -> { return annotationClass; }
            case "toString" -> { return "@" + annotationClass.getName() + annotationMethodValues; }
            case "hashCode" -> { return annotationMethodValues.hashCode(); }
            case "equals" -> { return o == objects[0]; }
        }

        return annotationMethodValues.get(methodName);
    }
}
