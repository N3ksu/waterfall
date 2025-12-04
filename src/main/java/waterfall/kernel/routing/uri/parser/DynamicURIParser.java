package waterfall.kernel.routing.uri.parser;

import waterfall.constant.WFConstant;

public final class DynamicURIParser implements URIParser {
    @Override
    public String parse(String uri) throws Exception {
        long openingBracesCount = uri.chars().filter(c -> c == '{').count();
        long closingBracesCount = uri.chars().filter(c -> c == '}').count();

        if (openingBracesCount != closingBracesCount)
            throw new Exception("Malformed URI" + uri);

        String regex = "\\{(?<i>" + WFConstant.JAVA_VAR_NOMENCLATURE_RGX  + ")}";
        String replacement = "(?<${i}>[^/]+?)";

        return "^" + uri.replaceAll(regex, replacement) + "$";
    }
}
