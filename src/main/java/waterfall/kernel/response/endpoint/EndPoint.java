package waterfall.kernel.response.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;

public interface EndPoint {
    EndPoint MV = new ModelViewEndPoint();
    EndPoint JSON = new JsonEndPoint();

    void forward(final HttpServletRequest req, final HttpServletResponse res, final Route route, final Object controller, final Object[] args) throws Exception;
}
