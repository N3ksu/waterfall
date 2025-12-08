package waterfall.kernel.response.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.response.json.JsonResponse;
import waterfall.kernel.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public final class JsonEndPoint implements EndPoint {
    @Override
    public void forward(final HttpServletRequest req, final HttpServletResponse res, final Route route, final Object controller, final Object[] args) throws Exception {
        res.setContentType("application/json;charset=UTF-8");

        try {
            final Method method = route.getAction();
            final Object actionResult = method.invoke(controller, args);
            final PrintWriter out = res.getWriter();
            out.print(JsonResponse.ok(actionResult).json());
        } catch (final Exception e) {
            final PrintWriter out = res.getWriter();
            out.print(JsonResponse.err(e).json());
        }
    }
}
