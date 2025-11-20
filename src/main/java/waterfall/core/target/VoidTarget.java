package waterfall.core.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class VoidTarget implements ITarget {
    @Override
    public void land(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter out = res.getWriter();
        Method method = route.getMethod();
        method.invoke(controller, args);

        res.setContentType("text/plain;charset=UTF-8");
        out.print("200 Invoke (void): " + method.getName());

        out.close();
    }
}
