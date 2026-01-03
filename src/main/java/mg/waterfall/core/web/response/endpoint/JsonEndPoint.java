package mg.waterfall.core.web.response.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.waterfall.core.web.response.json.Response;
import mg.waterfall.core.web.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public final class JsonEndPoint implements EndPoint {
    @Override
    public void forward(HttpServletRequest request, HttpServletResponse response, Route route, Object controller, Object[] args) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        try {
            Method method = route.getAction();
            Object actionResult = method.invoke(controller, args);
            PrintWriter out = response.getWriter();
            out.print(Response.ok(actionResult).json());
            out.close();
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.print(Response.err(e).json());
            out.close();
        }
    }
}
