package waterfall.core.destination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import waterfall.core.route.Route;

public interface Destination {
    Destination MODEL_VIEW = new ModelViewDestination();
    Destination STRING = new StringDestination();
    Destination VOID = new VoidDestination();

    void forward(Route route, Object[] args, Object controller, HttpServletRequest req, HttpServletResponse res) throws Exception;
}
