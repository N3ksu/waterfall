package waterfall.kernel.routing.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;

public interface EndPoint {
    EndPoint MV = new ModelViewEndPoint();
    EndPoint JSON = new JsonEndPoint();

    void forward(HttpServletRequest req, HttpServletResponse res, Route route, Object controller, Object[] args) throws Exception;
}
