package waterfall.kernel.util.string;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Set;

public final class StringToObjectConverter {
    private final Set<Class<?>> convertibleClasses;

    public StringToObjectConverter() {
        convertibleClasses = Set
                .of(String.class,
                int.class, long.class, double.class, float.class,
                Integer.class, Long.class, Double.class, Float.class,
                LocalDate.class, LocalTime.class, LocalDateTime.class, YearMonth.class);
    }

    public boolean isConvertible(Class<?> c) {
        return convertibleClasses.contains(c);
    }

    public Object convert(String[] s, Class<?> c) {
        if (c == null) return null;

        Object array = Array.newInstance(c, s.length);

        if (!isConvertible(c)) return array;

        for (int i = 0; i < s.length; i++)
            Array.set(array, i, convert(s[i], c));

        return array;
    }

    public Object convert(String s, Class<?> c) {
        if (s == null) return null;

        Object o = null;

        // String
        if (String.class.equals(c)) o = s;

        // Numeric value
        else if (int.class.equals(c) || Integer.class.equals(c)) o = Integer.valueOf(s);
        else if (long.class.equals(c) || Long.class.equals(c)) o = Long.valueOf(s);
        else if (double.class.equals(c) || Double.class.equals(c)) o = Double.valueOf(s);
        else if (float.class.equals(c) || Float.class.equals(c)) o = Float.valueOf(s);

        // Time related value
        else if (LocalDate.class.equals(c)) o = LocalDate.parse(s); // type="date"
        else if (LocalTime.class.equals(c)) o = LocalTime.parse(s); // type="time"
        else if (LocalDateTime.class.equals(c)) o = LocalDateTime.parse(s); // type="datetime-local"
        else if (YearMonth.class.equals(c)) o = YearMonth.parse(s); // type="month"

        return o;
    }
}
