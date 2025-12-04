package waterfall.kernel.dispatcher.destination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.kernel.routing.route.Route;

public interface Destination {
    Destination MV = new ModelViewDestination();
    Destination STR = new StringDestination();
    Destination VOID = new VoidDestination();

    void forward(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
