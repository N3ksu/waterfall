package waterfall.kernel.destination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class VoidDestination implements Destination {
    @Override
    public void forward(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        PrintWriter out = res.getWriter();

        Method method = route.getMethod();
        method.setAccessible(true);

        method.invoke(controller, args);

        res.setContentType("text/plain;charset=UTF-8");
        out.print("200 Invoke (void): " + method.getName());

        out.close();
    }
}
