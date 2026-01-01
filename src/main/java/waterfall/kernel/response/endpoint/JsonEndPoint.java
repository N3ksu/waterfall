package waterfall.kernel.response.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.response.json.Response;
import waterfall.kernel.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public final class JsonEndPoint implements EndPoint {
    @Override
    public void forward(HttpServletRequest req, HttpServletResponse res, Route route, Object controller, Object[] args) throws Exception {
        res.setContentType("application/json;charset=UTF-8");

        try {
            Method method = route.getAction();
            Object actionResult = method.invoke(controller, args);
            PrintWriter out = res.getWriter();
            out.print(Response.ok(actionResult).json());
            out.close();
        } catch (Exception e) {
            PrintWriter out = res.getWriter();
            out.print(Response.err(e).json());
            out.close();
        }
    }
}
