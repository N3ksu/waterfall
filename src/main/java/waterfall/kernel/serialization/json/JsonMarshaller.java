package waterfall.kernel.serialization.json;

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

public final class JsonMarshaller {
    private static final Set<Class<?>> SCALAR_CLASSES_SET = Set.of(
            String.class,
            Boolean.class,
            Integer.class, Long.class, Double.class, Float.class,
            LocalDate.class, LocalTime.class, LocalDateTime.class, YearMonth.class);

    public static String marshal(Object o) {
        StringBuilder json = new StringBuilder();
        marshalAndAppend(o, json);
        return json.toString();
    }

    private static boolean isScalar(Class<?> c) {
        return SCALAR_CLASSES_SET.contains(c);
    }

    private static void marshalAndAppend(Object o, StringBuilder b) {
        if (o == null) {
            b.append("null");
            return;
        }

        Class<?> c = o.getClass();

        if (isScalar(c)) marshalAndAppendScalar(o, b);
        else if (c.isArray()) marshalAndAppendArray(o, b);
        else if (Collection.class.isAssignableFrom(c)) marshalAndAppendCollection(o, b);
        else if (Map.class.isAssignableFrom(c)) marshalAndAppendMap(o, b);
        else marshalAndAppendObject(o, b);
    }

    private static void marshalAndAppendObject(Object o, StringBuilder b) {
        Class<?> c = o.getClass();

        b.append("{");
        boolean first = true;

        for (Field field : c.getDeclaredFields()) {
            String fieldName = field.getName();
            Method getter = ReflectionUtil.findGetter(field);

            if (getter != null) {
                try {
                    Object fieldValue = getter.invoke(o);
                    if (!first) b.append(",");
                    b.append(quote(fieldName)).append(":");
                    marshalAndAppend(fieldValue, b);
                    first = false;
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }

        b.append("}");
    }

    private static void marshalAndAppendScalar(Object s, StringBuilder b) {
        Class<?> c = s.getClass();

        if (Boolean.class.equals(c)) b.append(s);
        else if (Number.class.isAssignableFrom(c)) b.append(s);
        else b.append(quote(s.toString()));
    }

    private static void marshalAndAppendMap(Object m, StringBuilder b) {
        Map<?, ?> map = (Map<?, ?>) m;

        b.append("{");
        boolean first = true;

        for (Entry<?, ?> entry : map.entrySet()) {
            if (!first) b.append(",");
            b.append(quote(entry.getKey().toString())).append(":");
            marshalAndAppend(entry.getValue(), b);
            first = false;
        }

        b.append("}");
    }

    private static void marshalAndAppendCollection(Object c, StringBuilder b) {
        Collection<?> collection = (Collection<?>) c;

        b.append("[");
        boolean first = true;
        for (Object e : collection) {
            if (!first) b.append(",");
            marshalAndAppend(e, b);
            first = false;
        }
        b.append("]");
    }

    private static void marshalAndAppendArray(Object a, StringBuilder b) {
        int length = Array.getLength(a);

        b.append("[");
        boolean first = true;
        for (int i = 0; i < length; i++) {
            if (!first) b.append(",");
            marshalAndAppend(Array.get(a, i), b);
            first = false;
        }
        b.append("]");
    }

    private static String quote(String s) {
        return "\"" + s + "\"";
    }
}
