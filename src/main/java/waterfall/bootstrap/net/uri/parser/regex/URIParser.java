package waterfall.bootstrap.net.uri.parser.regex;

public interface URIParser {
    URIParser STATIC = new StaticURIParser();
    URIParser DYNAMIC = new DynamicURIParser();

    String parse(String uri);
}
