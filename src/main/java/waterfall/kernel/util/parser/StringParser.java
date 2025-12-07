package waterfall.kernel.util.parser;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

public final class StringParser {
    private final List<Class<?>> supportedClasses;

    public StringParser() {
        supportedClasses = List
                .of(String.class,
                int.class, long.class, double.class, float.class,
                Integer.class, Long.class, Double.class, Float.class,
                LocalDate.class, LocalTime.class, LocalDateTime.class, YearMonth.class);
    }

    public boolean isSupported(Class<?> c) {
        return supportedClasses.contains(c);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] parseStringArray(String[] s, Class<T> c) {
        if (c == null)
            return null;

        T[] array = (T[]) Array.newInstance(c, s.length);

        if (!isSupported(c))
            return array;

        for (int i = 0; i < s.length; i++)
            Array.set(array, i, parseString(s[i], c));

        return array;
    }

    @SuppressWarnings("unchecked")
    public <T> T parseString(String s, Class<T> c) {
        if (s == null) return null;

        Object o = null;

        // String
        if (c.equals(String.class)) o = s;

        // Numeric value
        else if (c.equals(int.class) || c.equals(Integer.class)) o =  Integer.valueOf(s);
        else if (c.equals(long.class) || c.equals(Long.class)) o = Long.valueOf(s);
        else if (c.equals(double.class) || c.equals(Double.class)) o =  Double.valueOf(s);
        else if (c.equals(float.class) || c.equals(Float.class)) o = Float.valueOf(s);

        // Time related value
        else if (c.equals(LocalDate.class)) o = LocalDate.parse(s); // type="date"
        else if (c.equals(LocalTime.class)) o =  LocalTime.parse(s); // type="time"
        else if (c.equals(LocalDateTime.class)) o = LocalDateTime.parse(s); // type="datetime-local"
        else if (c.equals(YearMonth.class)) o = YearMonth.parse(s); // type="month"

        if (o == null) return null;

        return (T) o;
    }
}
