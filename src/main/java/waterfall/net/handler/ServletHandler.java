package waterfall.net.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ServletHandler {
    ServletHandler IMPL = new ServletHandlerImpl();

    void handle(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
