package waterfall.api.annotation.request.mapping;

import waterfall.kernel.routing.http.HttpMethod;
import waterfall.kernel.meta.annotation.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

@Extends(RequestMapping.class)
@RequestMapping(method = HttpMethod.GET)
public @interface GetMapping {
    String value();
}
