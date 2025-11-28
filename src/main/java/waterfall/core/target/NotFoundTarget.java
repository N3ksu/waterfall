package waterfall.core.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.route.Route;

import java.io.PrintWriter;

public class NotFoundTarget implements Target {
    @Override
    public void land(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter out = res.getWriter();
        String path = req.getServletPath();

        res.setContentType("text/plain;charset=UTF-8");
        out.print("404 Not Found: " + path);

        out.close();
    }
}
