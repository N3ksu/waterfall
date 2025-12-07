package waterfall.kernel.web.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.kernel.constant.WFConstant;
import waterfall.kernel.dispatcher.WFDispatcher;
import waterfall.kernel.routing.router.Router;

import java.io.IOException;

@WebServlet(WFConstant.FRONT_SERVLET_URL_MAPPING)
public final class FrontServlet extends HttpServlet {
    private RequestDispatcher ctxDefaultDispatcher;
    private WFDispatcher WFDispatcher;

    @Override
    public void init()
            throws ServletException {
        ctxDefaultDispatcher = getServletContext()
                .getNamedDispatcher(WFConstant.SERVLET_CTX_DEFAULT_REQ_DISPATCHER_NAME);

        if (ctxDefaultDispatcher == null)
            throw new ServletException("The context's default dispatcher cannot be found");

        Router router = (Router) getServletContext()
                .getAttribute(WFConstant.ROUTER_CTX_ATTR_NAME);

        WFDispatcher = new WFDispatcher(router);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if (getServletContext().getResource(path) != null)
            ctxDefaultDispatcher.forward(req, res);

        else {
            try {
                WFDispatcher.forward(req, res);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
