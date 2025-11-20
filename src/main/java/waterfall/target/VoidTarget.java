package waterfall.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.bootstrap.net.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class VoidTarget implements Target {
    @Override
    public void land(Route route, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter out = res.getWriter();
        Method method = route.getMethod();

        res.setContentType("text/plain;charset=UTF-8");
        out.print("200 Invoke (void): " + method.getName());

        method.invoke(controller);
        out.close();
    }
}
