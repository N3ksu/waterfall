package waterfall.kernel.dispatcher.destination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class StringDestination implements Destination {
    @Override
    public void forward(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        PrintWriter out = res.getWriter();

        Method method = route.getMethod();
        method.setAccessible(true);

        String actionResult = (String) method.invoke(controller, args);

        res.setContentType("text/plain;charset=UTF-8");
        out.print("200 String: " + actionResult);

        out.close();
    }
}
