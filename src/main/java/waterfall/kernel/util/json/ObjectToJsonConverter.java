package waterfall.kernel.util.json;

import waterfall.kernel.meta.util.ReflectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class ObjectToJsonConverter {
    private final Set<Class<?>> scalarClasses;

    public ObjectToJsonConverter() {
        scalarClasses = Set
                .of(String.class,
                Boolean.class,
                Integer.class, Long.class, Double.class, Float.class,
                LocalDate.class, LocalTime.class, LocalDateTime.class, YearMonth.class);
    }

    public String convert(Object o) {
        StringBuilder json = new StringBuilder();
        convertAndAppend(o, json);
        return json.toString();
    }

    private boolean isScalar(Class<?> c) {
        return scalarClasses.contains(c);
    }

    private void convertAndAppend(Object o, StringBuilder b) {
        if (o == null) {
            b.append("null");
            return;
        }

        Class<?> c = o.getClass();

        if (isScalar(c)) convertAndAppendScalar(o, b);
        else if (c.isArray()) convertAndAppendArray(o, b);
        else if (Collection.class.isAssignableFrom(c)) convertAndAppendCollection(o, b);
        else if (Map.class.isAssignableFrom(c)) convertAndAppendMap(o, b);

        for (Field field : c.getDeclaredFields()) {
            String fieldName = field.getName();
            Method getter = ReflectionUtil.findGetter(field);

            if (getter != null) {
                try {
                    b.append(fieldName).append(": ");
                    convertAndAppend(getter.invoke(o), b);
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    b.append(fieldName).append(": ").append("null");
                }
            } else b.append(fieldName).append(": ").append("null");
        }
    }

    private void convertAndAppendScalar(Object s, StringBuilder b) {
        Class<?> c = s.getClass();

        if (Boolean.class.equals(c)) b.append(s);
        else if (Number.class.isAssignableFrom(c)) b.append(s);
        else b.append("\"").append(s).append("\"");
    }

    private void convertAndAppendMap(Object m, StringBuilder b) {
        Map<?, ?> map = (Map<?, ?>) m;

        b.append("{");
        boolean first = true;
        for (Entry<?, ?> entry : map.entrySet()) {
            if (!first) b.append(", ");
            b.append("\"").append(entry.getKey()).append("\": ");
            convertAndAppend(entry.getValue(), b);
        }
        b.append("}");
    }

    private void convertAndAppendCollection(Object c, StringBuilder b) {
        Collection<?> collection = (Collection<?>) c;

        b.append("[");
        boolean first = true;
        for (Object e : collection) {
            if (!first) b.append(", ");
            convertAndAppend(e, b);
            first = false;
        }
        b.append("]");
    }

    private void convertAndAppendArray(Object a, StringBuilder b) {
        int length = Array.getLength(a);

        b.append("[");
        boolean first = true;
        for (int i = 0; i < length; i++) {
            if (!first) b.append(", ");
            convertAndAppend(Array.get(a, i), b);
            first = false;
        }
        b.append("]");
    }
}
