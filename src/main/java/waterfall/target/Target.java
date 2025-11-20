package waterfall.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.bootstrap.net.route.Route;

import java.lang.reflect.Method;

public interface Target {
    Target VOID = new VoidTarget();
    Target STRING = new StringTarget();
    Target MODEL_VIEW = new ModelViewTarget();
    Target NOT_FOUND = new NotFoundTarget();

    void land(Route route, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
