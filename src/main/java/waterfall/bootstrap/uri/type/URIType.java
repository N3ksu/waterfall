package waterfall.bootstrap.uri.type;

import waterfall.bootstrap.uri.parser.regex.URIParser;

public enum URIType {
    STATIC(URIParser.STATIC),
    DYNAMIC(URIParser.DYNAMIC);

    private final URIParser parser;

    private URIType(URIParser parser) {
        this.parser = parser;
    }

    public URIParser getParser() {
        return parser;
    }

    public static URIType typeOf(String uri) {
        if (uri.contains("{") || uri.contains("}"))
            return DYNAMIC;
        return STATIC;
    }
}
