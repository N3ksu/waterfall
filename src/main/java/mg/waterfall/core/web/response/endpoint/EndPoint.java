package mg.waterfall.core.web.response.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.waterfall.core.web.routing.route.Route;

public interface EndPoint {
    EndPoint MV = new ModelViewEndPoint();
    EndPoint JSON = new JsonEndPoint();

    void forward(HttpServletRequest request, HttpServletResponse response, Route route, Object controller, Object[] args) throws Exception;
}
