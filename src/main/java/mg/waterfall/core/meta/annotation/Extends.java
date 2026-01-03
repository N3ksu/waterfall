package mg.waterfall.core.meta.annotation;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extends {
    Class<? extends Annotation> value();
}
