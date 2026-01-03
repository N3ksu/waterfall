package mg.waterfall.api.annotation.request.mapping;

import mg.waterfall.core.web.routing.http.method.HttpMethod;
import mg.waterfall.core.meta.annotation.Extends;

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
