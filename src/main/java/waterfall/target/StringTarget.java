package waterfall.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.bootstrap.net.route.Route;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringTarget implements Target {
    @Override
    public void land(Route route, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try (PrintWriter out = res.getWriter()) {
            Method method = route.getMethod();
            String actionResult = (String) method.invoke(controller);

            res.setContentType("text/plain;charset=UTF-8");
            out.print("200 String: " + actionResult);
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new Exception(e);
        }
    }
}
