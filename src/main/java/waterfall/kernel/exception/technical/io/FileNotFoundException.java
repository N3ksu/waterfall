package waterfall.kernel.exception.technical.io;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String path) {
        super("%s is nowhere to be found".formatted(path));
    }
}
