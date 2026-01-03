package mg.waterfall.core.exception.technical.routing;

public class MalformedURIException extends RuntimeException {
    public MalformedURIException(String uri) {
        super("Malformed URI %s".formatted(uri));
    }
}
