package mg.waterfall.core.jakarta.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.waterfall.core.config.ContextAccessor;
import mg.waterfall.core.web.request.dispatcher.RequestDispatcher;
import mg.waterfall.core.web.routing.router.Router;

import java.io.IOException;

@WebServlet(FrontServlet.FRONT_SERVLET_URL_MAPPING)
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10 * 1024 * 1024, maxRequestSize = 50 * 1024 * 1024)
public final class FrontServlet extends HttpServlet {
    private static final String SERVLET_CONTEXT_DEFAULT_REQUEST_DISPATCHER_NAME = "default";
    public static final String FRONT_SERVLET_URL_MAPPING = "/";

    private jakarta.servlet.RequestDispatcher contextDefaultDispatcher;
    private RequestDispatcher requestDispatcher;

    @Override
    public void init() throws ServletException {
        contextDefaultDispatcher = getServletContext().getNamedDispatcher(SERVLET_CONTEXT_DEFAULT_REQUEST_DISPATCHER_NAME);

        if (contextDefaultDispatcher == null)
            throw new ServletException("The context's default dispatcher cannot be found");

        ContextAccessor contextAccessor = ContextAccessor.getContextAccessor(getServletContext());
        Router router = contextAccessor.getRouter();
        requestDispatcher = new RequestDispatcher(router);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if (getServletContext().getResource(path) != null)
            contextDefaultDispatcher.forward(request, response);

        else {
            try {
                requestDispatcher.forward(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
