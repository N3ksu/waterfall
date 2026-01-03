package mg.waterfall.api.annotation.request.mapping;

import mg.waterfall.core.web.routing.http.method.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
    HttpMethod[] method() default { HttpMethod.GET, HttpMethod.POST };
}
