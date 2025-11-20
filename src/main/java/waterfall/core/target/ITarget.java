package waterfall.core.target;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.route.Route;


public interface ITarget {
    ITarget VOID = new VoidTarget();
    ITarget STRING = new StringTarget();
    ITarget MODEL_VIEW = new ModelViewTarget();
    ITarget NOT_FOUND = new NotFoundTarget();

    void land(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
