package waterfall.kernel.jakarta.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import waterfall.kernel.constant.Constant;
import waterfall.kernel.request.dispatcher.Dispatcher;
import waterfall.kernel.routing.router.Router;

import java.io.IOException;

@WebServlet(Constant.Jakarta.FRONT_SERVLET_URL_MAPPING)
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10 * 1024 * 1024, maxRequestSize = 50 * 1024 * 1024)
public final class FrontServlet extends HttpServlet {
    private RequestDispatcher contextDefaultDispatcher;
    private Dispatcher dispatcher;

    @Override
    public void init()
            throws ServletException {
        contextDefaultDispatcher = getServletContext().getNamedDispatcher(Constant.Jakarta.SERVLET_CTX_DEFAULT_REQ_DISPATCHER_NAME);

        if (contextDefaultDispatcher == null) throw new ServletException("The context's default dispatcher cannot be found");

        Router router = (Router) getServletContext()
                .getAttribute(Constant.Context.ROUTER_CTX_ATTR_NAME);

        dispatcher = new Dispatcher(router);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if (getServletContext().getResource(path) != null) contextDefaultDispatcher.forward(req, res);

        else {
            try {
                dispatcher.forward(req, res);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
