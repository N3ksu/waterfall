package mg.waterfall.core.exception.technical.meta;

import java.lang.reflect.Method;

public class MergedAnnotationLSPException extends RuntimeException {
    public MergedAnnotationLSPException(Method lower, Method higher) {
        super("%s.%s() return %s isn't compatible with parent %s.%s() return %s"
                .formatted(lower.getDeclaringClass().getName(), lower.getName(), lower.getReturnType().getName(),
                        higher.getDeclaringClass().getName(), higher.getName(), higher.getReturnType().getName()));
    }
}
