package waterfall.bootstrap.uri.parser.regex;

public final class DynamicURIParser implements URIParser {
    @Override
    public String parse(String uri) throws Exception {
        long openingBracesCount = uri.chars().filter(c -> c == '{').count();
        long closingBracesCount = uri.chars().filter(c -> c == '}').count();

        if (openingBracesCount != closingBracesCount)
            throw new Exception("Malformed URI" + uri);

        String regex = "\\{(?<i>[A-Za-z_$][A-Za-z0-9_$]*)}";
        String replacement = "(?<${i}>[^/]+?)";

        return "^" + uri.replaceAll(regex, replacement) + "$";
    }
}
