package waterfall.core.reflection.args;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public final class ArgumentParser {
    public Object parseString(String s, Class<?> c) {
        if (s == null)
            return null;

        // String
        if (c.equals(String.class))
            return s;

        // Numeric value
        if (c.equals(int.class) || c.equals(Integer.class))
            return Integer.parseInt(s);

        if (c.equals(long.class) || c.equals(Long.class))
            return Long.parseLong(s);

        if (c.equals(double.class) || c.equals(Double.class))
            return Double.parseDouble(s);

        if (c.equals(float.class) || c.equals(Float.class))
            return Float.parseFloat(s);

        // TODO Maybe we should provide the client a wrapper for those complex data type

        // Time related value
        if (c.equals(LocalDate.class))
            return LocalDate.parse(s); // date

        if (c.equals(LocalTime.class))
            return LocalTime.parse(s); // time

        if (c.equals(LocalDateTime.class))
            return LocalDateTime.parse(s); // datetime-local

        if (c.equals(YearMonth.class))
            return YearMonth.parse(s); // month

        return null; // Non-parsable String will be nullified
    }
}
