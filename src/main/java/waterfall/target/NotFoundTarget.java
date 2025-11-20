package waterfall.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.bootstrap.net.route.Route;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class NotFoundTarget implements Target {
    @Override
    public void land(Route route, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter out = res.getWriter()) {
            String path = req.getServletPath();

            res.setContentType("text/plain;charset=UTF-8");
            out.print("404 Not Found: " + path);
        }
    }
}
