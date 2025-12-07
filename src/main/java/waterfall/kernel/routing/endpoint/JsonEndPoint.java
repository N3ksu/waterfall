package waterfall.kernel.routing.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.util.json.ObjectToJsonConverter;
import waterfall.kernel.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public final class JsonEndPoint implements EndPoint {
    private final ObjectToJsonConverter objectToJsonConverter;

    public JsonEndPoint() {
        objectToJsonConverter = new ObjectToJsonConverter();
    }

    @Override
    public void forward(HttpServletRequest req, HttpServletResponse res, Route route, Object controller, Object[] args) throws Exception {
        try {
            Method method = route.getMethod();
            Object actionResult = method.invoke(controller, args);

            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();
        } catch (Exception e) {

        }
    }
}
