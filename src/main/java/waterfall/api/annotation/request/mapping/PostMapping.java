package waterfall.api.annotation.request.mapping;

import waterfall.api.http.HttpMethod;
import waterfall.kernel.annotation.MergeTo;

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
