package waterfall.kernel.constant;

import java.util.regex.Pattern;

public final class RegexConstant {
    public static final String JAVA_VAR_NOMENCLATURE_RGX = "[A-Za-z_$][A-Za-z0-9_$]*";
    public static final String ARRAY_NOTATION_RGX = "(" + JAVA_VAR_NOMENCLATURE_RGX + ")(?:\\[(\\d+)])?";
    public static final Pattern ARRAY_NOTATION_PATTERN = Pattern.compile(ARRAY_NOTATION_RGX);
}
