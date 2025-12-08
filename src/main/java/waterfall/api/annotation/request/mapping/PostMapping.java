package waterfall.api.annotation.request.mapping;

import waterfall.kernel.routing.http.method.HttpMethod;
import waterfall.kernel.meta.annotation.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

@Extends(RequestMapping.class)
@RequestMapping(method = HttpMethod.POST)
public @interface PostMapping {
    String value();
}
