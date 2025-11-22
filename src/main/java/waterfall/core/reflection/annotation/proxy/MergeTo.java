package waterfall.core.reflection.annotation.proxy;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MergeTo {
    Class<? extends Annotation> value();
}
