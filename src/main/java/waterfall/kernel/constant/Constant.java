package waterfall.kernel.constant;

import java.util.regex.Pattern;

public final class Constant {
    public static final class Context {
        public static final String ROUTER_CTX_ATTR_NAME = "wf.router";
    }

    public static final class Regex {
        public static final String JAVA_VAR_NOMENCLATURE_RGX = "[A-Za-z_$][A-Za-z0-9_$]*";
        public static final Pattern ROUTE_GRP_PATTERN = Pattern.compile("\\(\\?<(" + JAVA_VAR_NOMENCLATURE_RGX + ")>");
    }
}
