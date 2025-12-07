package waterfall.kernel.routing.uri.parser;

import waterfall.kernel.constant.WFConstant;

public final class DynamicURIParser implements URIParser {
    private final String regex;
    private final String replacement;

    public DynamicURIParser() {
        regex = "\\{(?<i>" + WFConstant.JAVA_VAR_NOMENCLATURE_RGX  + ")}";
        replacement = "(?<${i}>[^/]+?)";
    }

    @Override
    public String parse(String uri) throws Exception {
        long openingBracesCount = uri.chars().filter(c -> c == '{').count();
        long closingBracesCount = uri.chars().filter(c -> c == '}').count();

        if (openingBracesCount != closingBracesCount)
            throw new Exception("Malformed URI" + uri);

        return "^" + uri.replaceAll(regex, replacement) + "$";
    }
}
