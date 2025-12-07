package waterfall.kernel.routing.uri.parser;

public interface URIParser {
    URIParser STATIC = new StaticURIParser();
    URIParser DYNAMIC = new DynamicURIParser();

    String parse(String uri) throws Exception;
}
