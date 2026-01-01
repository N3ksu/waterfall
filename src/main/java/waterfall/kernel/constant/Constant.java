package waterfall.kernel.constant;

import java.util.regex.Pattern;

public final class Constant {
    public static final class Reflection {
        public static final String SETTER_PREFIX = "set";
        public static final String GETTER_PREFIX = "get";
        public static final String BOOLEAN_GETTER_PREFIX = "is";
    }

    public static final class Regex {
        public static final String JAVA_VAR_NOMENCLATURE_REGEX = "[A-Za-z_$][A-Za-z0-9_$]*";
        public static final Pattern ROUTE_GRP_PATTERN = Pattern.compile("\\(\\?<(" + JAVA_VAR_NOMENCLATURE_REGEX + ")>");
    }
}
