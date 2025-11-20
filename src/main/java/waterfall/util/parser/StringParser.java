package waterfall.util.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public final class StringParser {
    public static Object parse(String s, Class<?> c) {
        if (c.equals(int.class) || c.equals(Integer.class))
            return Integer.parseInt(s);

        if (c.equals(long.class) || c.equals(Long.class))
            return Long.parseLong(s);

        if (c.equals(float.class) || c.equals(Float.class))
            return Float.parseFloat(s);

        if (c.equals(double.class) || c.equals(Double.class))
            return Double.parseDouble(s);

        // TODO Maybe we should provide the client a wrapper for those complex data type
        if (c.equals(LocalTime.class))
            return LocalTime.parse(s); // time

        if (c.equals(YearMonth.class))
            return YearMonth.parse(s); // month

        if (c.equals(LocalDate.class))
            return LocalDate.parse(s); // date

        if (c.equals(LocalDateTime.class))
            return LocalDateTime.parse(s); // datetime-local

        return s; // If we cannot parse it into another class than String, we return the String itself
    }
}
