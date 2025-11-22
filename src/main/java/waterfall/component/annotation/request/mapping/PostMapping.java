package waterfall.component.annotation.request.mapping;

import waterfall.component.http.HttpMethod;
import waterfall.core.reflection.annotation.proxy.MergeTo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

@MergeTo(RequestMapping.class)
@RequestMapping(method = HttpMethod.POST)
public @interface PostMapping {
    String value();
}
