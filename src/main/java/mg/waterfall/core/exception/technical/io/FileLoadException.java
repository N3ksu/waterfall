package mg.waterfall.core.exception.technical.io;

public class FileLoadException extends RuntimeException {
    public FileLoadException(String path, Throwable cause) {
        super("When trying to load %s an exception occurred".formatted(path), cause);
    }
}
