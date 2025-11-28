package waterfall.core.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.route.Route;


public interface Target {
    Target VOID = new VoidTarget();
    Target STRING = new StringTarget();
    Target MODEL_VIEW = new ModelViewTarget();
    Target NOT_FOUND = new NotFoundTarget();

    void land(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
