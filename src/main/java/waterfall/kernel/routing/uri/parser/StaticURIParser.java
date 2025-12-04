package waterfall.kernel.routing.uri.parser;

public final class StaticURIParser implements URIParser {
    @Override
    public String parse(String uri) throws Exception {
        return "^" + uri + "$";
    }
}
