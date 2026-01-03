package mg.waterfall.core.exception.technical.meta;

public class NoArgsConstructorException extends RuntimeException {
    public NoArgsConstructorException(Class<?> clazz, Throwable cause) {
        super("Cannot get an instance of %s from its no arguments constructor".formatted(clazz.getName()), cause);
    }
}
