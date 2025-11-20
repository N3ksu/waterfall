package waterfall.bootstrap.uri.parser.regex;

public final class StaticURIParser implements URIParser {
    @Override
    public String parse(String uri) {
        return "^" + uri + "$";
    }
}
