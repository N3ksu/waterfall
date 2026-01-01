package waterfall.kernel.exception.technical.meta;

public class InstantiationNoArgsConstructorException extends RuntimeException {
    public InstantiationNoArgsConstructorException(Class<?> clazz, Throwable cause) {
        super("Cannot get an instance of %s from its no arguments constructor".formatted(clazz.getName()), cause);
    }
}
