package waterfall.kernel.routing.uri.util;

import waterfall.kernel.constant.Constant;

public final class UriRegexConverter {
    private static final String DYNAMIC_RGX = "\\{(?<i>" + Constant.Regex.JAVA_VAR_NOMENCLATURE_RGX  + ")}";
    private static final String DYNAMIC_REPLACEMENT = "(?<${i}>[^/]+?)";

    public static String convert(String uri) throws Exception {
        if (uri == null) return null;

        if (uri.contains("{") || uri.contains("}")) {
            long opening = uri.chars().filter(c -> c == '{').count();
            long closing = uri.chars().filter(c -> c == '}').count();

            if (opening != closing) throw new Exception("Malformed URI" + uri);

            return uri.replaceAll(DYNAMIC_RGX, DYNAMIC_REPLACEMENT);
        }
        return "^" + uri + "$";
    }
}
