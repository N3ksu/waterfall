package waterfall.kernel.constant;

import java.util.regex.Pattern;

public final class Constant {
    public static final class Config {
        public static final String CONFIG_FILE_URI = "/WEB-INF/waterfall.properties";
        public static final String CONTROLLER_PACKAGE_CONFIG_PARAM_NAME = "controller.package";
    }

    public static final class Context {
        public static final String ROUTER_CTX_ATTR_NAME = "wf.router";
    }

    public static final class Jakarta {
        public static final String SERVLET_CTX_DEFAULT_REQ_DISPATCHER_NAME = "default";
        public static final String FRONT_SERVLET_URL_MAPPING = "/";
    }

    public static final class Reflection {
        public static final String SETTER_PREFIX = "set";
        public static final String GETTER_PREFIX = "get";
        public static final String BOOLEAN_GETTER_PREFIX = "is";
    }

    public static final class Regex {
        public static final String JAVA_VAR_NOMENCLATURE_RGX = "[A-Za-z_$][A-Za-z0-9_$]*";
        public static final String ARRAY_NOTATION_RGX = "(" + JAVA_VAR_NOMENCLATURE_RGX + ")(?:\\[(\\d+)])?";
        public static final Pattern ARRAY_NOTATION_PATTERN = Pattern.compile(ARRAY_NOTATION_RGX);
        public static final Pattern ROUTE_GRP_PATTERN = Pattern.compile("\\(\\?<(" + JAVA_VAR_NOMENCLATURE_RGX + ")>");
    }
}
