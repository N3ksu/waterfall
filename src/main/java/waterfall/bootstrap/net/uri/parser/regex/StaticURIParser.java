package waterfall.bootstrap.net.uri.parser.regex;

public class StaticURIParser implements URIParser {
    @Override
    public String parse(String uri) {
        return "^" + uri + "$";
    }
}
