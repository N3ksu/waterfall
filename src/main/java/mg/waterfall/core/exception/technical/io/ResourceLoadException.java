package mg.waterfall.core.exception.technical.io;

public class ResourceLoadException extends RuntimeException {
    public ResourceLoadException(String path, Throwable cause) {
        super("An Exception occurred when trying to load a resource at %s".formatted(path), cause);
    }
}
